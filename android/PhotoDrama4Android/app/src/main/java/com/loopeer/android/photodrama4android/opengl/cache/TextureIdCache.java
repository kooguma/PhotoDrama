package com.loopeer.android.photodrama4android.opengl.cache;


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
        return mIdsMap.get(key);
    }

    public void addIdToCache(int key, int id) {
        mIdsMap.put(key, id);
    }

}
