package com.loopeer.android.photodrama4android.opengl.model;


public class SubtitleClip extends Clip{

    public String content;

    public SubtitleClip(String content, int startTime) {
        this.content = content;
        this.startTime = startTime;
    }
}
