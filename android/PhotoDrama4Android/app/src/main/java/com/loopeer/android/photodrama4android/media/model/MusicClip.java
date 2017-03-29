package com.loopeer.android.photodrama4android.media.model;


public class MusicClip extends Clip {
    public String path;

    public int musicStartOffset;
    public int musicSelectedLength;

    public MusicType musicType;

    public enum MusicType{BGM, RECORD_MUSIC, SOUND};

    public int getEndTime() {
        return showTime + startTime - 1;
    }

    public int getSeekTime(int usedTime) {
        return (usedTime - startTime) % musicSelectedLength + musicStartOffset;
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
        return startTime == ((ImageClip)obj).startTime
                && path.equals(((ImageClip)obj).path);
    }
}
