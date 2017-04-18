package com.loopeer.android.photodrama4android.media.recorder;

import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class AudioMixer {
    private static final String TAG = "AudioMixer";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public List<Integer> mTimeClips = new ArrayList<>();
    public MuxingCallback mMuxingCallback;
    public List<MusicBufferClipProcessor> mMusicBufferClipProcessors = new ArrayList<>();

    public AudioMixer(List<MusicClip> musicClips, MuxingCallback muxingCallback) {
        TreeSet<Integer> timeClips = new TreeSet<>();
        for (MusicClip musicClip : musicClips) {
            timeClips.add(musicClip.startTime * 1000);
            timeClips.add(musicClip.getEndTime() * 1000);
            mMusicBufferClipProcessors.add(new MusicBufferClipProcessor(musicClip));
        }

        mTimeClips.addAll(timeClips);
        mMuxingCallback = muxingCallback;
    }

    public void startMux() {
        for (int i = 0; i < mTimeClips.size() - 1; i++) {
            int startTime = mTimeClips.get(i);
            int endTime = mTimeClips.get(i + 1);
            if (DEBUG) Log.e(TAG, "processClip time : " + startTime + " : " + endTime);
            processClip(startTime, endTime);
        }
        mMuxingCallback.onMuxData(null, 0, mTimeClips.get(mTimeClips.size() - 1));
    }

    private void processClip(int startTime, int endTime) {
        int timeOffset = startTime;
        byte[][] audioBytes = new byte[3][];
        while (timeOffset < endTime) {
            long timelength = endTime - timeOffset > AudioBufferTimeParser.BUFFER_SIZE_TIME_LENGTH
                    ? AudioBufferTimeParser.BUFFER_SIZE_TIME_LENGTH : endTime - timeOffset;
            int mergeStreamCount = 0;
            for (MusicBufferClipProcessor processor :
                    mMusicBufferClipProcessors) {
                if (DEBUG) Log.e(TAG, "processor read time : " + timeOffset + " : " + timelength);
                byte[] data = processor.read(timeOffset, timelength);
                if (data != null) {
                    if (audioBytes[0] == null) {
                        audioBytes[0] = Arrays.copyOf(data, data.length);
                        if (DEBUG) Log.e(TAG, "count 1");
                        mergeStreamCount++;
                    }
                    if (audioBytes[1] == null) {
                        audioBytes[1] = Arrays.copyOf(data, data.length);
                        mergeStreamCount++;
                    }
                    if (audioBytes[2] == null) {
                        audioBytes[2] = Arrays.copyOf(data, data.length);
                        mergeStreamCount++;
                    }
                } else {
                    if (DEBUG) Log.e(TAG, "read data null");
                }
            }
            byte[][] audioMergeBytes = new byte[mergeStreamCount][];
            for (int i = 0; i < mergeStreamCount; i++) {
                audioMergeBytes[i] = audioBytes[i];
            }
            byte[] mixBytes = mixRawAudioBytes(audioMergeBytes);
            if (mixBytes != null) {
                if (DEBUG) Log.e(TAG, "onMuxData data :  " + mixBytes.length + " :  offset " + timeOffset);
                mMuxingCallback.onMuxData(mixBytes, mixBytes.length, timeOffset);
            } else {
                if (DEBUG) Log.e(TAG, "mix null  " );
            }
            timeOffset += timelength;
            audioBytes[0] = null;
            audioBytes[1] = null;
            audioBytes[2] = null;
        }
    }

    public interface MuxingCallback {
        void onMuxData(byte[] data, int length, long presentationTimeUs);
    }

    byte[] mixRawAudioBytes(byte[][] bMulRoadAudioes) {

        if (bMulRoadAudioes == null || bMulRoadAudioes.length == 0)
            return null;

        byte[] realMixAudio = bMulRoadAudioes[0];

        if (bMulRoadAudioes.length == 1)
            return realMixAudio;

        for (int rw = 0; rw < bMulRoadAudioes.length; ++rw) {
            if (bMulRoadAudioes[rw].length != realMixAudio.length) {
                return null;
            }
        }

        int row = bMulRoadAudioes.length;
        int coloum = realMixAudio.length / 2;
        short[][] sMulRoadAudioes = new short[row][coloum];

        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < coloum; ++c) {
                sMulRoadAudioes[r][c] = (short) ((bMulRoadAudioes[r][c * 2] & 0xff) | (bMulRoadAudioes[r][c * 2 + 1] & 0xff) << 8);
            }
        }

        short[] sMixAudio = new short[coloum];
        int mixVal;
        int sr = 0;
        for (int sc = 0; sc < coloum; ++sc) {
            mixVal = 0;
            sr = 0;
            for (; sr < row; ++sr) {
                mixVal += sMulRoadAudioes[sr][sc];
            }
            sMixAudio[sc] = (short) (mixVal / row);
        }

        for (sr = 0; sr < coloum; ++sr) {
            realMixAudio[sr * 2] = (byte) (sMixAudio[sr] & 0x00FF);
            realMixAudio[sr * 2 + 1] = (byte) ((sMixAudio[sr] & 0xFF00) >> 8);
        }

        return realMixAudio;
    }
}
