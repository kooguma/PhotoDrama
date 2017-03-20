package com.loopeer.android.photodrama4android.opengl.model;


import com.laputapp.model.BaseModel;

public class ScaleTranslateRatio extends BaseModel {

    public float scaleFactor = 1f;
    public float x;
    public float y;

    public ScaleTranslateRatio(float scaleFactor, float x, float y) {
        this.scaleFactor = scaleFactor;
        this.x = x;
        this.y = y;
    }
}
