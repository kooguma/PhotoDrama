package com.loopeer.android.photodrama4android.media.model;


import com.laputapp.model.BaseModel;
import com.loopeer.android.photodrama4android.media.mediaio.XmlScaleTransition;

public class ScaleTranslateRatio extends BaseModel {

    public float scaleFactor = 1f;
    public float x;
    public float y;

    public ScaleTranslateRatio() {}

    public ScaleTranslateRatio(float scaleFactor, float x, float y) {
        this.scaleFactor = scaleFactor;
        this.x = x;
        this.y = y;
    }

    public XmlScaleTransition toXml() {
        XmlScaleTransition xmlScaleTransition = new XmlScaleTransition();
        xmlScaleTransition.scale = scaleFactor;
        xmlScaleTransition.transitionX = x;
        xmlScaleTransition.transitionY = y;
        return xmlScaleTransition;
    }
}
