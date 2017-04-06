package com.loopeer.android.photodrama4android.media.mediaio;

import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.model.TransitionType;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Transition")
public class XmlTransition {

    @Element(name = "Id")
    public int id;

    @Element(name = "StartTime")
    public int startTime;

    @Element(name = "Duration")
    public int duration;

    public TransitionClip toObject() {
        TransitionClip transitionClip = new TransitionClip();
        transitionClip.transitionType = TransitionType.fromValue(id);
        transitionClip.startTime = startTime;
        transitionClip.showTime = duration;
        return transitionClip;
    }
}
