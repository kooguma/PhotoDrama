package com.loopeer.android.photodrama4android.media.mediaio;


import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.File;

@Root(name = "MusicClip")
public class XmlMusicClip {

    @Element(name = "Path")
    public String path;

    @Element(name = "Type")
    public int type;

    @Element(name = "StartTime")
    public int startTime;

    @Element(name = "Duration")
    public int duration;

    @Element(name = "Volume")
    public float volume;

    @Element(name = "StartOffset")
    public int startOffset;

    @Element(name = "CutDuration")
    public int cutDuration;

    public MusicClip toObject(String xmlPackage) {
        MusicClip musicClip = new MusicClip();
        musicClip.path = ZipUtils.pathFromPackageFile(xmlPackage, path);
        musicClip.musicType = MusicClip.MusicType.values()[type];
        musicClip.startTime = startTime;
        musicClip.startTime = duration;
        musicClip.volume = volume;
        musicClip.musicStartOffset = startOffset;
        musicClip.musicSelectedLength = cutDuration;
        return musicClip;
    }
}
