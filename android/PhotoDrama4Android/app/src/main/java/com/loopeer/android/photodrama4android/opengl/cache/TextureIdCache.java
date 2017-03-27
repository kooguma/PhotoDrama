package com.loopeer.android.photodrama4android.opengl.cache;


import android.util.Log;

import java.util.HashMap;

public class TextureIdCache {

    private static volatile TextureIdCache sDefaultInstance;
    private HashMap<Integer, Integer> mIdsMap;

    private TextureIdCache() {
        mIdsMap = new HashMap<>();
    }

    public static TextureIdCache getInstance() {
        if (sDefaultInstance == null) {
            synchronized (TextureIdCache.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new TextureIdCache();
                }
            }
        }
        return sDefaultInstance;
    }

    public int getTextureId(int key) {
        if (mIdsMap == null) return 0;
        Integer i = mIdsMap.get(key);
        Log.e("11111", i + " : ");
        return i;
    }

    public void addIdToCache(int key, int id) {
        mIdsMap.put(key, id);
    }

}
