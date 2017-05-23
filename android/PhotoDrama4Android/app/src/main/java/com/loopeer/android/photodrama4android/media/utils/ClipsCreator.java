package com.loopeer.android.photodrama4android.media.utils;

import com.loopeer.android.photodrama4android.media.model.Clip;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.model.ScaleTranslateRatio;
import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.media.model.VideoGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ClipsCreator {

    public static final float MIN_SCALE = 1.0f;
    public static final float MAX_SCALE = 1.5f;

    public static final float MIN_POS_TRANS = -0.05f;
    public static final float MAX_POS_TRANS = 0.05f;

    public static ArrayList<ImageClip> createImageClips(List<String> urls) {
        ArrayList<ImageClip> imageClips = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            ScaleTranslateRatio large = getTransition();
            ScaleTranslateRatio deft = getTransition();

            ImageClip imageClip;
            if (imageClips.isEmpty()) {
                imageClip = new ImageClip(urls.get(i), 0);
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

    public static ScaleTranslateRatio getTransition() {
        Random random = new Random();
        ScaleTranslateRatio transition = new ScaleTranslateRatio();
        transition.scaleFactor = random.nextFloat() * (MAX_SCALE - MIN_SCALE) + MIN_SCALE;
        transition.x = random.nextFloat() * (MAX_POS_TRANS - MIN_POS_TRANS) + MIN_POS_TRANS;
        transition.y = random.nextFloat() * (MAX_POS_TRANS - MIN_POS_TRANS) + MIN_POS_TRANS;
        return transition;
    }

    public static List<TransitionClip> createTransitionClips(List<ImageClip> imageClips) {
        List<TransitionClip> transitionClips = new ArrayList<>();
        for (int i = 0; i < imageClips.size() - 1; i++) {
            ImageClip imageClip = imageClips.get(i);
            ImageClip nextImageClip = imageClips.get(i + 1);
            TransitionClip transitionClip = new TransitionClip(imageClip.getEndTime() + 1);
            nextImageClip.startWithPreTransitionTime = nextImageClip.startTime;
            nextImageClip.startTime += transitionClip.showTime;
            imageClip.endWithNextTransitionTime = imageClip.getEndTime() + transitionClip.showTime;
            transitionClips.add(transitionClip);
        }
        return transitionClips;
    }

    public static void updateImageTransitionClips(VideoGroup videoGroup) {
        List<ImageClip> imageClips = videoGroup.imageClips;
        List<TransitionClip> transitionClips = videoGroup.transitionClips;

        for (int i = 0; i < imageClips.size() - 1; i++) {
            ImageClip imageClip = imageClips.get(i);
            ImageClip nextImageClip = imageClips.get(i + 1);
            TransitionClip transitionClip = transitionClips.get(i);
            imageClip.endWithNextTransitionTime = imageClip.getEndTime() + transitionClip.showTime;
            transitionClip.startTime = imageClip.getEndTime() + 1;
            nextImageClip.startWithPreTransitionTime = imageClip.getEndTime() + 1;
            nextImageClip.startTime = imageClip.endWithNextTransitionTime + 1;
            nextImageClip.endWithNextTransitionTime = nextImageClip.getEndTime();
        }
    }

    public static void updateImageClipsByShowTime(VideoGroup videoGroup) {
        List<ImageClip> imageClips = videoGroup.imageClips;
        List<TransitionClip> transitionClips = videoGroup.transitionClips;

        for (int i = 0; i < imageClips.size() - 1; i++) {
            ImageClip imageClip = imageClips.get(i);
            ImageClip nextImageClip = imageClips.get(i + 1);
            TransitionClip transitionClip = transitionClips.get(i);
            imageClip.endWithNextTransitionTime = imageClip.getEndTime() + transitionClip.showTime;
            transitionClip.startTime = imageClip.getEndTime() + 1;
            nextImageClip.startWithPreTransitionTime = imageClip.getEndTime() + 1;
            nextImageClip.startTime = imageClip.endWithNextTransitionTime + 1;
            nextImageClip.endWithNextTransitionTime = nextImageClip.getEndTime();
        }
    }

    public static List<TransitionImageWrapper> createTransiImageWrappers(VideoGroup videoGroup) {
        List<Clip> clips = new ArrayList<>();
        clips.addAll(videoGroup.imageClips);
        clips.addAll(videoGroup.transitionClips);
        Collections.sort(clips, (o1, o2) -> {
            int x1 = o1.startTime;
            int y1 = o2.startTime;
            if (x1 < y1) return -1;
            if (x1 > y1) return 1;
            if (x1 == y1 && o1 instanceof TransitionClip) return -1;
            return 1;
        });
        List<TransitionImageWrapper> transitionImageWrappers = new ArrayList<>();
        for (Clip clip : clips) {
            TransitionImageWrapper t;
            if (clip instanceof ImageClip) {
                t = new TransitionImageWrapper((ImageClip) clip);
            } else {
                t = new TransitionImageWrapper((TransitionClip) clip);
            }
            transitionImageWrappers.add(t);
        }
        return transitionImageWrappers;
    }

    public static List<TransitionImageWrapper> getTransiImageClipsNoEmpty(VideoGroup videoGroup) {
        List<Clip> clips = new ArrayList<>();
        clips.addAll(videoGroup.imageClips);
        clips.addAll(videoGroup.transitionClips);
        Collections.sort(clips, (o1, o2) -> {
            int x1 = o1.startTime;
            int y1 = o2.startTime;
            if (x1 < y1) return -1;
            if (x1 > y1) return 1;
            if (x1 == y1 && o1 instanceof TransitionClip) return -1;
            return 1;
        });
        List<TransitionImageWrapper> transitionImageWrappers = new ArrayList<>();
        for (Clip clip : clips) {
            if (clip.showTime == 0) continue;
            TransitionImageWrapper t;
            if (clip instanceof ImageClip) {
                t = new TransitionImageWrapper((ImageClip) clip);
            } else {
                t = new TransitionImageWrapper((TransitionClip) clip);
                t.transitionPreImagePath = transitionImageWrappers.get(transitionImageWrappers.size() - 1).imageClip.path;
            }
            transitionImageWrappers.add(t);
        }
        return transitionImageWrappers;
    }
}
