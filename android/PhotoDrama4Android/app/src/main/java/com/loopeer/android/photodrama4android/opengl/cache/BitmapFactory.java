package com.loopeer.android.photodrama4android.opengl.cache;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import com.loopeer.android.librarys.imagegroupview.utils.ImageUtils;

public class BitmapFactory {

    private static volatile BitmapFactory sDefaultInstance;
    private LruCache<String, Bitmap> mMemoryCache;

    private Context mContext;

    private BitmapFactory(Context context) {
        mContext = context;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static BitmapFactory init(Context context) {
        if (sDefaultInstance == null) {
            synchronized (BitmapFactory.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new BitmapFactory(context);
                }
            }
        }
        return sDefaultInstance;
    }

    public static BitmapFactory getInstance() {
        if (sDefaultInstance == null) {
            try {
                throw new Exception("Instance have not getInstance");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sDefaultInstance;
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void loadImages(String... args) {
        new BitmapWorkerTask().execute(args);
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            for (String path :
                    params) {
                final String imageKey = String.valueOf(path);
                final Bitmap bitmap = ImageUtils.imageZoomByScreen(mContext, imageKey);
                addBitmapToCache(imageKey, bitmap);
            }
            return null;
        }
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

}
