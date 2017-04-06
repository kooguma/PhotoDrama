package com.loopeer.android.photodrama4android.media.mediaio;


import com.loopeer.android.photodrama4android.media.model.MusicClip;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

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

    public MusicClip toObject() {
        MusicClip musicClip = new MusicClip();
        musicClip.path = path;
        musicClip.musicType = MusicClip.MusicType.values()[type];
        musicClip.startTime = startTime;
        musicClip.startTime = duration;
        musicClip.volume = volume;
        musicClip.musicStartOffset = startOffset;
        musicClip.musicSelectedLength = cutDuration;
        return musicClip;
    }
}
