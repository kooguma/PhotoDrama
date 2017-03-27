package com.loopeer.android.photodrama4android.opengl.model;


import com.laputapp.model.BaseModel;

public class TransitionImageWrapper extends BaseModel {

    public ImageClip imageClip;
    public TransitionClip transitionClip;

    public String transitionPreImagePath;

    public TransitionImageWrapper(ImageClip imageClip) {
        this.imageClip = imageClip;
    }

    public TransitionImageWrapper(TransitionClip transitionClip) {
        this.transitionClip = transitionClip;
    }

    public boolean isImageClip() {
        return imageClip != null;
    }
}
