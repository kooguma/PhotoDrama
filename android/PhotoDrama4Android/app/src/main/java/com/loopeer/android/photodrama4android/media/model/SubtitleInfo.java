package com.loopeer.android.photodrama4android.media.model;


import java.io.Serializable;

public class SubtitleInfo implements Serializable{
    public int textureObjectId = -1;
    public int width;
    public int height;
    public String content;

    public SubtitleInfo(int width, int height, String content) {
        this.width = width;
        this.height = height;
        this.content = content;
    }

    public SubtitleInfo(int textureObjectId, int width, int height) {
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
