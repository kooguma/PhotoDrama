package com.loopeer.android.photodrama4android.media.recorder;
import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.utils.FileManager;
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
            int length;
            long pointer = mAudioFileStreams.getFilePointer();
            long offset = 0, lastLength = 0;
            if (pointer + dataLength > mAudioFileStreams.length()) {
                offset = mAudioFileStreams.length() - pointer;
                lastLength = (pointer + dataLength) % mAudioFileStreams.length();
            }
            if ((length = mAudioFileStreams.read(buffer)) != -1) {
                if (DEBUG) Log.e(TAG, "mAudioFileStreams.read(buffer): " + timeOffsetUs +  " : "  + timeLengthUs);
                return buffer;
            } else {
                if (DEBUG) Log.e(TAG, "mAudioFileStreams.read(buffer): " + offset +  " : "  + lastLength + " : " + timeOffsetUs + " : " + timeLengthUs);

                if (length < dataLength) {
                    mAudioFileStreams.seek(0);
                    mAudioFileStreams.read(buffer, (int)offset, (int)lastLength);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
