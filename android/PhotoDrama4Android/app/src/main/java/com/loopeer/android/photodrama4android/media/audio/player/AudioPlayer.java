package com.loopeer.android.photodrama4android.media.audio.player;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    private AudioTrack mAudioTrack;                          // AudioTrack
    private AudioParams mAudioParams;                          // Attributes

    private byte[] mBytes;                                   // 音频数据
    private int mPrimePlaySize = 0;                          // 较优播放块大小
    private int mPlayOffset = 0;                             // 当前播放位置  需要根据时间计算？

    private boolean mThreadExitFlag;                          // 线程退出标志
    private WorkThread mWorkThread;                          // 工作线程

    private int mTotalTime;

    private AudioTrack.OnPlaybackPositionUpdateListener mPlaybackPositionUpdateListener;
    private boolean playing;

    public AudioPlayer() {
    }

    public void setAttributes(AudioParams attributes) {
        this.mAudioParams = attributes;
    }

    public void setDataSource(byte[] bytes) throws IOException {
        mBytes = bytes;
    }

    // private byte[] getAudioBytes(String path) throws IOException {
    //     File file = new File(path);
    //     FileInputStream is = new FileInputStream(file);
    //     return inputStreamToByte(is);
    // }

    public void setPlaybackPositionUpdateListener(AudioTrack.OnPlaybackPositionUpdateListener listener) {
        this.mPlaybackPositionUpdateListener = listener;
    }

    public void prepare() {
        if (mBytes == null) return;
        createAudioTrack();
    }

    private void createAudioTrack() {

        if (mAudioParams == null) {
            mAudioParams = new AudioParams.Builder().build();
        }

        // 获得构建对象的最小缓冲区大小
        int minBufSize = AudioTrack.getMinBufferSize(
            mAudioParams.mParams.mFrequency,
            mAudioParams.mParams.mChannel,
            mAudioParams.mParams.mSampleBit);

        mPrimePlaySize = minBufSize * 2;

        Log.e(TAG, "primePlaySize = " + mPrimePlaySize);

        mTotalTime = (int) (mBytes.length / mAudioParams.mParams.mFrequency / 4.0f * 1000);

        Log.e(TAG, "bytes length  = " + mBytes.length);

        Log.e(TAG, "total time = " + mTotalTime);

        mThreadExitFlag = true;

        mAudioTrack = new AudioTrack(
            mAudioParams.mParams.mSteamType,
            mAudioParams.mParams.mFrequency,
            mAudioParams.mParams.mChannel,
            mAudioParams.mParams.mSampleBit,
            minBufSize,
            mAudioParams.mParams.mMode);

        mAudioTrack.setPositionNotificationPeriod(44100); // 44100 / s 88200

        mAudioTrack.setNotificationMarkerPosition(44100 * 5);

        mAudioTrack.setPlaybackPositionUpdateListener(mPlaybackPositionUpdateListener);

    }

    public void reset() {
        mThreadExitFlag = false;
        mBytes = null;
        mTotalTime = 0;
        mPlayOffset = 0;
        mPrimePlaySize = 0;
    }

    public void seekTo(int time) {
        mAudioTrack.pause();
        if (mTotalTime != 0) {
            float p =  time * 1.0f / mTotalTime ;
            Log.e(TAG, "p =" + p);
            mPlayOffset = (int) (p * mBytes.length);
            Log.e(TAG, "play offset =" + mPlayOffset);
        } else {
            mPlayOffset = 0;
        }
        mAudioTrack.play();
    }

    public void play() {
        mPlayOffset = 0;
        startWorkThread();
    }

    public void pause() {
        if (mAudioTrack != null) {
            mAudioTrack.pause();
            stopWorkThread();
        }
    }

    public void stop() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            stopWorkThread();
        }
    }

    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public int getTotalTime() {
        return mTotalTime;
    }

    private void startWorkThread() {
        if (mWorkThread == null) {
            mThreadExitFlag = false;
            mWorkThread = new WorkThread();
            mWorkThread.start();
        }
    }

    private void stopWorkThread() {
        if (mWorkThread != null) {
            mThreadExitFlag = true;
            mWorkThread = null;
        }
    }

    public boolean isPlaying() {
        return !mThreadExitFlag;
    }

    class WorkThread extends Thread {

        private static final String TAG = "PlayAudioThread";

        @Override public void run() {

            if (mAudioTrack != null) {

                mAudioTrack.play();

                while (!mThreadExitFlag) {
                    try {

                        int ret = mAudioTrack.write(mBytes, mPlayOffset,
                            mPrimePlaySize);

                        mPlayOffset += mPrimePlaySize;

                        Log.e(TAG, "run playOffset = " + mPlayOffset);

                        switch (ret) {
                            case AudioTrack.ERROR_INVALID_OPERATION:
                                Log.w(TAG, "play fail: ERROR_INVALID_OPERATION");
                            case AudioTrack.ERROR_BAD_VALUE:
                                Log.w(TAG, "play fail: ERROR_BAD_VALUE");
                            case AudioManager.ERROR_DEAD_OBJECT:
                                Log.w(TAG, "play fail: ERROR_DEAD_OBJECT");
                            default:
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w(TAG, "play fail: " + e.getMessage());
                        break;
                    }

                    if (mPlayOffset >= mBytes.length) {
                        mThreadExitFlag = true;
                    } else {
                        mThreadExitFlag = false;
                    }
                }

                mAudioTrack.stop();

                Log.e(TAG, "PlayAudioThread complete...");

            }
        }
    }

}
