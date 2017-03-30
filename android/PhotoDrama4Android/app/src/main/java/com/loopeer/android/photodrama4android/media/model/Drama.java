package com.loopeer.android.photodrama4android.media.model;


import com.laputapp.model.BaseModel;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;

import java.util.ArrayList;
import java.util.List;

public class Drama extends BaseModel{
    public AudioGroup audioGroup;
    public VideoGroup videoGroup;

    public Drama() {
        audioGroup = new AudioGroup();
        videoGroup = new VideoGroup();
    }

    public static Drama createFromPath(List<String> urls) {
        Drama drama = new Drama();
        VideoGroup videoGroup = new VideoGroup();
        videoGroup.imageClips = ClipsCreator.createImageClips(urls);
        videoGroup.transitionClips = ClipsCreator.createTransitionClips(videoGroup.imageClips);
        drama.videoGroup = videoGroup;
        return drama;
    }

    public int getShowTimeTotal() {
        ImageClip imageClip = videoGroup.imageClips.get(videoGroup.imageClips.size() - 1);
        return imageClip.getEndTime();
    }
}
