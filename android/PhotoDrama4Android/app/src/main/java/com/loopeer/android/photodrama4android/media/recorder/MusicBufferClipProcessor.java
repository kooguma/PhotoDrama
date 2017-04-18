package com.loopeer.android.photodrama4android.media.recorder;


import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.utils.FileManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MusicBufferClipProcessor {

    private static final String TAG = "MusicClipProcessor";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public MusicClip mMusicClip;
    RandomAccessFile mAudioFileStreams;


    public MusicBufferClipProcessor(MusicClip musicClip) {
        mMusicClip = musicClip;
        try {
            mAudioFileStreams = new RandomAccessFile(FileManager.getInstance().getDecodeAudioFilePath(musicClip), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public byte[] read(long timeOffsetUs, long timeLengthUs) {
        if (timeOffsetUs < mMusicClip.startTime * 1000 || timeOffsetUs > mMusicClip.getEndTime() * 1000) return null;
        int dataLength = AudioBufferTimeParser.getDataOffset(timeLengthUs);
        int dataOffset = AudioBufferTimeParser.getDataOffset((timeOffsetUs - mMusicClip.startTime * 1000) % (mMusicClip.musicSelectedLength * 1000));
        byte[] buffer = new byte[dataLength];
        try {
//            mAudioFileStreams.seek(dataOffset);
            if (mAudioFileStreams.read(buffer) != -1) {
                return buffer;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
