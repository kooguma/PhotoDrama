package com.loopeer.android.photodrama4android.media.cache;

import com.loopeer.android.photodrama4android.media.model.Drama;

import java.util.HashMap;

public class DramaCache {

    private static volatile DramaCache sDefaultInstance;
    private HashMap<String, Drama> mDramaHashMap;

    private DramaCache() {
        mDramaHashMap = new HashMap<>();
    }

    public static DramaCache getInstance() {
        if (sDefaultInstance == null) {
            synchronized (TextureIdCache.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new DramaCache();
                }
            }
        }
        return sDefaultInstance;
    }

    public Drama getDrama(String key) {
        if (mDramaHashMap == null) return null;
        Drama i = mDramaHashMap.get(key);
        return i;
    }

    public void putDrama(String key, Drama drama) {
        mDramaHashMap.put(key, drama);
    }

}
