package com.loopeer.android.photodrama4android.media.model;


public class MusicClip extends Clip {
    public String path;
    public int startTime = 0;
    public int showTime = 2000;

    public int musicStartOffset;
    public int musicEndOffset;

    public MusicType musicType;

    public enum MusicType{BGM, RECORD_MUSIC, SOUND};

    public int getEndTime() {
        return showTime + startTime - 1;
    }

    @Override
    public String toString() {
        return "MusicClip{" +
                "path='" + path + '\'' +
                ", startTime=" + startTime +
                ", showTime=" + showTime +
                ", musicStartOffset=" + musicStartOffset +
                ", musicEndOffset=" + musicEndOffset +
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
