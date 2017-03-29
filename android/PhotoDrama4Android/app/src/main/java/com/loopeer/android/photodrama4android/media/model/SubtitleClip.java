package com.loopeer.android.photodrama4android.media.model;


public class SubtitleClip extends Clip{
    public static final int MIN_SUBTITLE_LENGTH = 500;

    public String content;

    public SubtitleClip() {
        super();
        showTime = MIN_SUBTITLE_LENGTH;
    }

    public SubtitleClip(String content, int startTime) {
        this();
        this.content = content;
        this.startTime = startTime;
    }
}
