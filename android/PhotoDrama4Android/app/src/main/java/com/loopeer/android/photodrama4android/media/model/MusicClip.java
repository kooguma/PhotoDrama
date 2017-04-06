package com.loopeer.android.photodrama4android.media.model;


import com.loopeer.android.photodrama4android.media.mediaio.XmlMusicClip;

public class MusicClip extends Clip {
    public static final int MIN_RECORD_AUDIO_LENGTH = 500;
    public static final int MIN_SOUND_EFFECT_LENGTH = 500;
    public static final int MIN_BGM_LENGTH = 500;
    public String path;

    public float volume = 5.0f;

    public int musicStartOffset;
    public int musicSelectedLength;

    public MusicType musicType;

    private boolean isCreateIng = false;

    public enum MusicType{BGM, SOUND_EFFECT, RECORD_AUDIO};

    public MusicClip() {}

    public MusicClip(int startTime, MusicType type) {
        this.startTime = startTime;
        this.showTime = 0;
        this.musicType = type;
    }

    public int getEndTime() {
        return showTime + startTime - 1;
    }

    public int getSeekTime(int usedTime) {
        return (usedTime - startTime) % musicSelectedLength + musicStartOffset;
    }

    public boolean isCreateIng() {
        return isCreateIng;
    }

    public void setCreateIng(boolean createIng) {
        isCreateIng = createIng;
    }

    @Override
    public String toString() {
        return "MusicClip{" +
                "path='" + path + '\'' +
                ", startTime=" + startTime +
                ", showTime=" + showTime +
                ", musicStartOffset=" + musicStartOffset +
                ", musicSelectedLength=" + musicSelectedLength +
                ", musicType=" + musicType +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return getKey().equals(((MusicClip)obj).getKey());
    }

    public XmlMusicClip toXml() {
        XmlMusicClip xmlMusicClip = new XmlMusicClip();
        xmlMusicClip.path = path;
        xmlMusicClip.type = musicType.ordinal();
        xmlMusicClip.startTime = startTime;
        xmlMusicClip.duration = startTime;
        xmlMusicClip.volume = volume;
        xmlMusicClip.startOffset = musicStartOffset;
        xmlMusicClip.cutDuration = musicSelectedLength;
        return xmlMusicClip;
    }
}
