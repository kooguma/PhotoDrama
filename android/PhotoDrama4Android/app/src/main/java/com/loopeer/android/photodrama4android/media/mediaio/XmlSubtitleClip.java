package com.loopeer.android.photodrama4android.media.mediaio;


import com.loopeer.android.photodrama4android.media.model.SubtitleClip;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "SubtitleClip")
public class XmlSubtitleClip {

    @Element(name = "Content")
    public String content;

    @Element(name = "StartTime")
    public int startTime;

    @Element(name = "Duration")
    public int duration;

    public XmlSubtitleClip() {

    }

    public SubtitleClip toObject() {
        SubtitleClip subtitleClip = new SubtitleClip();
        subtitleClip.content = content;
        subtitleClip.startTime = startTime;
        subtitleClip.showTime = duration;
        return subtitleClip;
    }
}
