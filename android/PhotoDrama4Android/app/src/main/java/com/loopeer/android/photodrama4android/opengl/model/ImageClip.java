package com.loopeer.android.photodrama4android.opengl.model;


import com.laputapp.model.BaseModel;

public class ImageClip extends BaseModel{
    public String path;
    public int startTime = 0;
    public int showTime = 2000;

    public ImageClip(String path) {
        this.path = path;
    }

    public ImageClip(String path, int startTime) {
        this.path = path;
        this.startTime = startTime;
    }

    public int getEndTime() {
        return showTime + startTime - 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return path.equals(((ImageClip)obj).path);
    }
}
