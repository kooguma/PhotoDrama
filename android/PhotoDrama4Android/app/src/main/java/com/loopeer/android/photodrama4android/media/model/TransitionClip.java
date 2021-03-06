package com.loopeer.android.photodrama4android.media.model;


import com.loopeer.android.photodrama4android.media.mediaio.XmlTransition;

import static com.loopeer.android.photodrama4android.media.Constants.DEFAULT_TRANSITION_CLIP_SHOW_TIME;

public class TransitionClip extends Clip{
    public TransitionType transitionType;

    public TransitionClip() {
        showTime = DEFAULT_TRANSITION_CLIP_SHOW_TIME;
    }

    public TransitionClip(int startTime) {
        this();
        this.startTime = startTime;
        showTime = 0;
        transitionType = TransitionType.NO;
    }

    public TransitionClip(TransitionType transitionType) {
        this();
        this.transitionType = transitionType;
    }

    public XmlTransition toXml() {
        XmlTransition xmlTransition = new XmlTransition();
        xmlTransition.id = transitionType.getValue();
        xmlTransition.startTime = startTime;
        xmlTransition.duration = showTime;
        return xmlTransition;
    }
}
