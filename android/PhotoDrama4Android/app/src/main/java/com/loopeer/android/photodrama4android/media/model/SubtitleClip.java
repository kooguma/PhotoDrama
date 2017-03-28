package com.loopeer.android.photodrama4android.media.model;


public class SubtitleClip extends Clip{

    public String content;

    public SubtitleClip(String content, int startTime) {
        this.content = content;
        this.startTime = startTime;
    }
}
