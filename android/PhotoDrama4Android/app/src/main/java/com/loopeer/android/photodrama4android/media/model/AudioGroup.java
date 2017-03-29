package com.loopeer.android.photodrama4android.media.model;


import com.laputapp.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class AudioGroup extends BaseModel {

    public List<MusicClip> musicClips;

    public AudioGroup() {
        this.musicClips = new ArrayList<>();
    }
}
