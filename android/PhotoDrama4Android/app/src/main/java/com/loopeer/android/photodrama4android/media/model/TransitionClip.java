package com.loopeer.android.photodrama4android.media.model;


public class TransitionClip extends Clip{
    public TransitionType transitionType;

    public TransitionClip(int startTime) {
        this.startTime = startTime;
        showTime = 0;
        transitionType = TransitionType.NO;
    }

    public TransitionClip(TransitionType transitionType) {
        this.transitionType = transitionType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return transitionType.getValue() == (((TransitionClip)obj).transitionType.getValue());
    }
}
