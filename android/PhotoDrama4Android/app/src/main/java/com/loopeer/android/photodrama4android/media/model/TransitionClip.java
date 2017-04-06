package com.loopeer.android.photodrama4android.media.model;


import com.loopeer.android.photodrama4android.media.mediaio.XmlTransition;

public class TransitionClip extends Clip{
    public TransitionType transitionType;

    public TransitionClip() {
    }

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

    public XmlTransition toXml() {
        XmlTransition xmlTransition = new XmlTransition();
        xmlTransition.id = transitionType.getValue();
        xmlTransition.startTime = startTime;
        xmlTransition.duration = showTime;
        return xmlTransition;
    }
}
