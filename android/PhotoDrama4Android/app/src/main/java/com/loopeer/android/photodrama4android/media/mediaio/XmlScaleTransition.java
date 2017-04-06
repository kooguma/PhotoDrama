package com.loopeer.android.photodrama4android.media.mediaio;

import com.loopeer.android.photodrama4android.media.model.ScaleTranslateRatio;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "ScaleTransition")
public class XmlScaleTransition {

    @Element(name = "Scale")
    public float scale;

    @Element(name = "TransitionX")
    public float transitionX;

    @Element(name = "TransitionY")
    public float transitionY;

    public ScaleTranslateRatio toObject() {
        ScaleTranslateRatio scaleTranslateRatio = new ScaleTranslateRatio();
        scaleTranslateRatio.scaleFactor = scale;
        scaleTranslateRatio.x = transitionX;
        scaleTranslateRatio.y = transitionY;
        return scaleTranslateRatio;
    }
}
