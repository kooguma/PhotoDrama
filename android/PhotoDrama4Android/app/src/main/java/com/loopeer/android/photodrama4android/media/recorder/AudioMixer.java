package com.loopeer.android.photodrama4android.media.recorder;

import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class AudioMixer {
    private static final String TAG = "AudioMixer";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final int MULTI_CHANNEL_COUNT = 3;
    private static final float VOLUME_ADJUST_PARAM = 1f;

    public List<Integer> mTimeClips = new ArrayList<>();
    public MuxingCallback mMuxingCallback;
    public List<MusicBufferClipProcessor> mMusicBufferClipProcessors = new ArrayList<>();
    public AudioMixer(Drama drama, MuxingCallback muxingCallback) {
        TreeSet<Integer> timeClips = new TreeSet<>();
        timeClips.add(0);
        for (MusicClip musicClip : drama.audioGroup.musicClips) {
            timeClips.add(musicClip.startTime * 1000);
            timeClips.add(musicClip.getEndTime() * 1000);
            mMusicBufferClipProcessors.add(new MusicBufferClipProcessor(musicClip));
        }
        timeClips.add(drama.getShowTimeTotal() * 1000);
        mTimeClips.addAll(timeClips);
        mMuxingCallback = muxingCallback;
    }

    public void startMux() {
        if (DEBUG) Log.e(TAG, mTimeClips.toString());

        for (int i = 0; i < mTimeClips.size() - 1; i++) {
            int startTime = mTimeClips.get(i);
            int endTime = mTimeClips.get(i + 1);
            if (i != mTimeClips.size() - 2) {
                endTime--;
            }
            processClip(startTime, endTime);
        }
        if (DEBUG) Log.e(TAG, "Mux Finished");

        mMuxingCallback.onMuxData(null, 0, mTimeClips.get(mTimeClips.size() - 1));
    }

    private void processClip(int startTime, int endTime) {
        int timeOffset = startTime;
        byte[][] audioBytes = new byte[3][];
        float[] volumes = new float[]{1f, 1f, 1f};
        while (timeOffset < endTime) {
            long timelength = endTime - timeOffset > AudioBufferTimeParser.BUFFER_SIZE_TIME_LENGTH
                    ? AudioBufferTimeParser.BUFFER_SIZE_TIME_LENGTH : endTime - timeOffset;
            int mergeStreamCount = 0;
            for (MusicBufferClipProcessor processor :
                    mMusicBufferClipProcessors) {
                byte[] data = processor.read(timeOffset, timelength);
                float volume = processor.getVolume(timeOffset, timelength);
                if (data != null) {
                    if (audioBytes[0] == null) {
                        audioBytes[0] = Arrays.copyOf(data, data.length);
                        volumes[0] = volume;
                        mergeStreamCount++;
                        continue;
                    }
                    if (audioBytes[1] == null) {
                        audioBytes[1] = Arrays.copyOf(data, data.length);
                        volumes[1] = volume;
                        mergeStreamCount++;
                        continue;
                    }
                    if (audioBytes[2] == null) {
                        audioBytes[2] = Arrays.copyOf(data, data.length);
                        volumes[1] = volume;
                        mergeStreamCount++;
                        continue;
                    }
                } else {
                }
            }
            byte[][] audioMergeBytes = new byte[mergeStreamCount][];
            for (int i = 0; i < mergeStreamCount; i++) {
                audioMergeBytes[i] = audioBytes[i];
            }
            byte[] mixBytes;
            if (mergeStreamCount == 0) {
                int dataLength = AudioBufferTimeParser.getDataOffset(timelength);
                mixBytes = new byte[dataLength];
            } else {
                mixBytes = mixAudioBytes(audioMergeBytes, volumes);
            }
            if (mixBytes != null) {
                mMuxingCallback.onMuxData(mixBytes, mixBytes.length, timeOffset);
            }
            timeOffset += timelength;
            audioBytes[0] = null;
            audioBytes[1] = null;
            audioBytes[2] = null;
            volumes[0] = 1f;
            volumes[1] = 1f;
            volumes[2] = 1f;
        }
    }

    public interface MuxingCallback {
        void onMuxData(byte[] data, int length, long presentationTimeUs);
    }

    byte[] mixAudioBytes(byte[][] audioes, float[] volumes) {

        if (audioes == null || audioes.length == 0)
            return null;

        byte[] resultAudio = audioes[0];

        for (int rw = 0; rw < audioes.length; ++rw) {
            if (audioes[rw].length != resultAudio.length) {
                return null;
            }
        }

        int row = audioes.length;
        int coloum = resultAudio.length / 2;
        short[][] sAudioes = new short[row][coloum];

        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < coloum; ++c) {
                sAudioes[r][c] = (short) ((audioes[r][c * 2] & 0xff) | (audioes[r][c * 2 + 1] & 0xff) << 8);
            }
        }

        short[] sMixAudio = new short[coloum];
        int mixVal;
        int sr;
        for (int sc = 0; sc < coloum; ++sc) {
            mixVal = 0;
            sr = 0;
            for (; sr < row; ++sr) {
                int v = sAudioes[sr][sc];
                mixVal += (int)(VOLUME_ADJUST_PARAM * volumes[sr] * v);
            }
            sMixAudio[sc] = (short) (mixVal / MULTI_CHANNEL_COUNT);
        }

        for (sr = 0; sr < coloum; ++sr) {
            resultAudio[sr * 2] = (byte) (sMixAudio[sr] & 0x00FF);
            resultAudio[sr * 2 + 1] = (byte) ((sMixAudio[sr] & 0xFF00) >> 8);
        }

        return resultAudio;
    }
}
