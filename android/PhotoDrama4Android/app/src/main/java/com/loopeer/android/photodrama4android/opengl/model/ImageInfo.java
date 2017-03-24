package com.loopeer.android.photodrama4android.opengl.model;


import java.io.Serializable;

public class ImageInfo implements Serializable{
    public int textureObjectId = -1;
    public int width;
    public int height;

    public ImageInfo(int textureObjectId, int width, int height) {
        this.textureObjectId = textureObjectId;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "textureObjectId=" + textureObjectId +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
