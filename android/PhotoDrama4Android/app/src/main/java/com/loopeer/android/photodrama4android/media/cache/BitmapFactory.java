package com.loopeer.android.photodrama4android.media.cache;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.loopeer.android.photodrama4android.utils.LocalImageUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class BitmapFactory {

    private static volatile BitmapFactory sDefaultInstance;
    private LinkedHashMap<String, Bitmap> mMemoryCache;

    private Context mContext;

    private BitmapFactory(Context context) {
        mContext = context;
        mMemoryCache = new LinkedHashMap<>();
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

    public boolean contains(String key) {
        return mMemoryCache.containsKey(key);
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
                final Bitmap bitmap = LocalImageUtils.imageZoomByScreen(mContext, imageKey);
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

    public void clear() {
        for (Map.Entry<String, Bitmap> entry : mMemoryCache.entrySet()) {
            entry.getValue().recycle();
        }
        mMemoryCache.clear();
    }

}
