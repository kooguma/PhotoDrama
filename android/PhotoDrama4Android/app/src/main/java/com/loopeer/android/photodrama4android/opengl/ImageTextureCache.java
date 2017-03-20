package com.loopeer.android.photodrama4android.opengl;


import com.loopeer.android.photodrama4android.opengl.model.ImageInfo;

import java.util.LinkedHashMap;

public class ImageTextureCache {

    public LinkedHashMap<String, ImageInfo> imagesInfos = new LinkedHashMap<>();

    private static volatile ImageTextureCache sDefaultInstance;

    private ImageTextureCache() {
    }

    public static ImageTextureCache getDefault() {
        if (sDefaultInstance == null) {
            synchronized (ImageTextureCache.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new ImageTextureCache();
                }
            }
        }
        return sDefaultInstance;
    }

    public ImageInfo getImageInfo(String key) {
        if (imagesInfos.containsKey(key))
//            return imagesInfos.get(key);
            return null;

        return null;
    }

    public void putImageInfo(String key, ImageInfo imageInfo) {
        imagesInfos.put(key, imageInfo);
    }
}
