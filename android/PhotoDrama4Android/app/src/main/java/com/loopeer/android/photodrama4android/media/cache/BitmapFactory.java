package com.loopeer.android.photodrama4android.media.cache;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.utils.LocalImageUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BitmapFactory {
    private static final String TAG = "BitmapFactory";

    private static volatile BitmapFactory sDefaultInstance;
        private LinkedHashMap<String, Bitmap> mMemoryCache;
    private LinkedHashMap<String, Bitmap> mMemoryCacheBlurImage;
    private static final int BLUR_RADIUS = 20;

    private Context mContext;

    private BitmapFactory(Context context) {
        mContext = context;
        mMemoryCache = new LinkedHashMap<>();
        mMemoryCacheBlurImage = new LinkedHashMap<>();
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
            init(PhotoDramaApp.getAppContext());
        }
        return sDefaultInstance;
    }

    public Bitmap getBitmapFromMemCacheNotCreate(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        return bitmap;
    }

    public Bitmap getBitmapFromMemCache(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = LocalImageUtils.imageZoomByScreen(mContext, key);
            addBitmapToCache(key, bitmap);
//            getBlurBitmapFromCache(key, bitmap);
        }
        return bitmap;
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
                Bitmap bitmapPre = LocalImageUtils.imageZoomByScreen(mContext, path);
                if (bitmapPre == null || bitmapPre.isRecycled()) {
                    final Bitmap bitmap = LocalImageUtils.imageZoomByScreen(mContext, path);
                    addBitmapToCache(path, bitmap);
                    getBlurBitmapFromCache(path, bitmap);
                }
            }
            return null;
        }
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBlurBitmapFromCache(String key, Bitmap bitmap) {
        Bitmap resultBitmap = mMemoryCacheBlurImage.get(key);
        if (resultBitmap == null || bitmap.isRecycled()) {
            resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            RenderScript rs = RenderScript.create(mContext);
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, resultBitmap);
            blurScript.setRadius(BLUR_RADIUS);
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);
            allOut.copyTo(resultBitmap);
            addBlurBitmapToCache(key, resultBitmap);
        }
        return resultBitmap;
    }

    public void addBlurBitmapToCache(String key, Bitmap bitmap) {
        mMemoryCacheBlurImage.put(key, bitmap);
    }

    public void removeBitmapToCache(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        mMemoryCache.remove(key);
        if (mMemoryCacheBlurImage.containsKey(key)) mMemoryCacheBlurImage.remove(key);
    }

    public void clear() {
        for (Map.Entry<String, Bitmap> entry : mMemoryCache.entrySet()) {
            entry.getValue().recycle();
        }
        mMemoryCache.clear();

        for (Map.Entry<String, Bitmap> entry : mMemoryCacheBlurImage.entrySet()) {
            entry.getValue().recycle();
        }
        mMemoryCacheBlurImage.clear();
    }

    public void clear(Drama drama) {
        List<String> removingKeys = new ArrayList<>();
        for (Map.Entry<String, Bitmap> entry : mMemoryCache.entrySet()) {
            boolean contain = false;
            for (ImageClip imageClip : drama.videoGroup.imageClips) {
                if (imageClip.path.equals(entry.getKey())) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                entry.getValue().recycle();
                removingKeys.add(entry.getKey());
            }
        }
        for (int i = 0; i < removingKeys.size(); i++) {
            mMemoryCache.remove(removingKeys.get(i));
            if (mMemoryCacheBlurImage.containsKey(removingKeys.get(i)))
                mMemoryCacheBlurImage.remove(removingKeys.get(i));
        }
    }

}
