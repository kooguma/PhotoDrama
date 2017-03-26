package com.loopeer.android.photodrama4android.opengl.model;


import com.laputapp.model.BaseModel;

public class Clip extends BaseModel {
    public int startTime = 0;
    public int showTime = 2000;

    public int getEndTime() {
        return showTime + startTime - 1;
    }

    @Override
    public String toString() {
        return "Clip{" +
                "startTime=" + startTime +
                ", showTime=" + showTime +
                '}';
    }
}
