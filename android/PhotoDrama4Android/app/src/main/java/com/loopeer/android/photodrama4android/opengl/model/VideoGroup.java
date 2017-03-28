package com.loopeer.android.photodrama4android.opengl.model;


import com.laputapp.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class VideoGroup extends BaseModel{
    public List<ImageClip> imageClips;
    public List<TransitionClip> transitionClips;
    public List<SubtitleClip> subtitleClips;

    public VideoGroup() {
        this.imageClips = new ArrayList<>();
        this.transitionClips = new ArrayList<>();
        this.subtitleClips = new ArrayList<>();
    }

}
