package com.loopeer.android.photodrama4android.media.mediaio;

import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.File;

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

    public ImageClip toObject(String xmlPackage) {
        ImageClip imageClip = new ImageClip();
        imageClip.path = ZipUtils.pathFromPackageFile(xmlPackage, path);
        imageClip.startTime = startTime;
        imageClip.showTime = duration;
        imageClip.startScaleTransRatio = startTransition.toObject();
        imageClip.endScaleTransRatio = endTransition.toObject();
        return imageClip;
    }
}
