package com.loopeer.android.photodrama4android.opengl.utils;

import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.model.ScaleTranslateRatio;

import java.util.ArrayList;
import java.util.List;

public class ImageClipsCreater {

    public static ArrayList<ImageClip> createImageClips(List<String> urls) {
        ArrayList<ImageClip> imageClips = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            ScaleTranslateRatio large = new ScaleTranslateRatio(3f, 0f, 0f);
            ScaleTranslateRatio deft = new ScaleTranslateRatio(2.5f, 0f, 0f);
            ImageClip imageClip;
            if (imageClips.isEmpty()) {
                imageClip = new ImageClip(urls.get(i));
            } else {
                ImageClip preImageClip = imageClips.get(i - 1);
                imageClip = new ImageClip(urls.get(i), preImageClip.showTime + preImageClip.startTime);
            }
            if (i % 2 == 0) {
                imageClip.startScaleTransRatio = large;
                imageClip.endScaleTransRatio = deft;
            } else {
                imageClip.startScaleTransRatio = deft;
                imageClip.endScaleTransRatio = large;
            }
            imageClips.add(imageClip);
        }
        return imageClips;
    }
}
