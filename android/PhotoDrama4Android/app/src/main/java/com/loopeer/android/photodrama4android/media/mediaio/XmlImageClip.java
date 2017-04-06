package com.loopeer.android.photodrama4android.media.mediaio;

import com.loopeer.android.photodrama4android.media.model.ImageClip;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "ImageClip")
public class XmlImageClip {
    @Element(name = "Path")
    public String path;

    @Element(name = "StartTime")
    public int startTime;

    @Element(name = "Duration")
	public int duration;

    @Element(name = "StartTransition")
	public XmlScaleTransition startTransition;

    @Element(name = "EndTransition")
    public XmlScaleTransition endTransition;

    public ImageClip toObject() {
        ImageClip imageClip = new ImageClip();
        imageClip.path = path;
        imageClip.startTime = startTime;
        imageClip.showTime = duration;
        imageClip.startScaleTransRatio = startTransition.toObject();
        imageClip.endScaleTransRatio = endTransition.toObject();
        return imageClip;
    }
}
