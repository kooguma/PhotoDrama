package com.loopeer.android.photodrama4android.media.model;


import com.loopeer.android.photodrama4android.media.mediaio.XmlSubtitleClip;

public class SubtitleClip extends Clip{
    public static final int MIN_SUBTITLE_LENGTH = 500;

    public String content;

    public boolean showEditRect = false;

    public SubtitleClip() {
        super();
        showTime = MIN_SUBTITLE_LENGTH;
    }

    public SubtitleClip(String content, int startTime) {
        this();
        this.content = content;
        this.startTime = startTime;
    }

    public XmlSubtitleClip toXml() {
        XmlSubtitleClip xmlSubtitleClip = new XmlSubtitleClip();
        xmlSubtitleClip.content = content;
        xmlSubtitleClip.startTime = startTime;
        xmlSubtitleClip.duration = showTime;
        return xmlSubtitleClip;
    }
}
