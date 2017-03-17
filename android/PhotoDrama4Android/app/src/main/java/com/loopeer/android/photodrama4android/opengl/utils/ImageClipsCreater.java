package com.loopeer.android.photodrama4android.opengl.utils;

import com.loopeer.android.photodrama4android.opengl.model.ImageClip;

import java.util.ArrayList;
import java.util.List;

public class ImageClipsCreater {

    public static ArrayList<ImageClip> createImageClips(List<String> urls) {
        ArrayList<ImageClip> imageClips = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            ImageClip imageClip;
            if (imageClips.isEmpty()) {
                imageClip = new ImageClip(urls.get(i));
            } else {
                ImageClip preImageClip = imageClips.get(i - 1);
                imageClip = new ImageClip(urls.get(i), preImageClip.showTime + preImageClip.startTime);
            }
            imageClips.add(imageClip);
        }
        return imageClips;
    }
}
