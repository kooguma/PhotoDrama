package com.loopeer.android.photodrama4android.media.audio.player;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;
import android.util.Log;
import android.util.LogPrinter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.media.AudioTrack.PLAYSTATE_PAUSED;
import static android.media.AudioTrack.PLAYSTATE_PLAYING;
import static android.media.AudioTrack.PLAYSTATE_STOPPED;
import static android.media.AudioTrack.STATE_INITIALIZED;

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    public static class State {
        public static final int UNPREPARED = 0x00001; //未就绪
        public static final int PREPARED = 0x00010; //已经就绪
        public static final int PLAYING = 0x00100; //播放中
        public static final int PAUSE = 0x01000; //暂停
        public static final int STOP = 0x10000; //暂停
    }

    private int mState; // maintain the state ourselves , do not use the state of AudioTrack

    private AudioTrack mAudioTrack;                          // AudioTrack
    private AudioParams mAudioParams;                          // Attributes

    private byte[] mBytes;                                   // 音频数据
    private int mPrimePlaySize = 0;                          // 较优播放块大小
    private int mPlayOffset = 0;                             // 当前播放位置  需要根据时间计算？

    private boolean mThreadExitFlag;                          // 线程退出标志
    private WorkThread mWorkThread;                          // 工作线程

    private int mTotalTime;

    private AudioTrack.OnPlaybackPositionUpdateListener mPlaybackPositionUpdateListener;

    public AudioPlayer() {
        reset();
    }

    public void setAttributes(AudioParams attributes) {
        this.mAudioParams = attributes;
    }

    public void setDataSource(byte[] bytes) throws IOException {
        mBytes = bytes;
    }

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

        mPrimePlaySize = minBufSize;

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

        mState = State.PREPARED;
    }

    public boolean isPrepared() {
        return mAudioTrack != null && mBytes != null && mBytes.length != 0;
    }

    public boolean isPlaying() {
        return mAudioTrack != null && mState == State.PLAYING;
    }

    public boolean isPause() {
        return mAudioTrack != null && mState == State.PAUSE;
    }

    public boolean isStop() {
        return mAudioTrack != null && mState == State.STOP;
    }

    public void reset() {
        mThreadExitFlag = false;
        mState = State.UNPREPARED;
        mBytes = null;
        mTotalTime = 0;
        mPlayOffset = 0;
        mPrimePlaySize = 0;
    }

    public synchronized void seekTo(int time) {
        Log.e(TAG, "seek to " + " time = " + time);
        if (mAudioTrack != null) {
            mAudioTrack.pause();
            if (mTotalTime != 0) {
                float p = time * 1.0f / mTotalTime;
                mPlayOffset = (int) (p * mBytes.length);
                //猜想：mPlayoffset 必须是 minBufSize 的整数倍
                mPlayOffset = (mPlayOffset / mPrimePlaySize) * mPrimePlaySize;
            } else {
                mPlayOffset = 0;
            }
            mAudioTrack.play();
        }
    }

    public void play() {
        Log.e(TAG, "play : state = " + mState);
        if (mAudioTrack != null) {
            if (checkState(State.PREPARED | State.STOP)) {
                Log.e(TAG, "1");
                mPlayOffset = 0;
                mState = State.PLAYING;
                mAudioTrack.play();
                startWorkThread();
            } else if (checkState(State.PLAYING | State.PAUSE)) {
                Log.e(TAG, "2");
                mState = State.PLAYING;
                mAudioTrack.play();
                if (mWorkThread != null && mWorkThread.isAlive()) {
                    Log.e(TAG, "2.1");
                    synchronized (mWorkThread) {
                        mWorkThread.notify();
                    }
                }
            } else {
                throw new IllegalStateException("current state : " + mState);
            }
        }
    }

    public void pause() {
        if (mAudioTrack != null) {
            mState = State.PAUSE;
            mAudioTrack.pause();
        }
    }

    public void stop() {
        if (mAudioTrack != null) {
            mState = State.STOP;
            mAudioTrack.stop();
            stopWorkThread();
        }
    }

    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
            stopWorkThread();
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

    private boolean checkState(int stateExpected) {
        return (mState & (stateExpected)) > 0;
    }

    class WorkThread extends Thread {

        private static final String TAG = "PlayAudioThread";

        @Override public void run() {

            Log.e(TAG, "mAudioTrack = null" + (mAudioTrack == null));
            if (mAudioTrack != null) {

                while (!mThreadExitFlag) {
                    synchronized (this) {
                        try {

                            Log.e(TAG, "isPlaying = " + isPlaying());

                            if (!isPlaying()) {
                                this.wait();
                            }

                            int ret = mAudioTrack.write(mBytes, mPlayOffset,
                                mPrimePlaySize);

                            mPlayOffset += mPrimePlaySize;

                            Log.e(TAG, "run playOffset = " + mPlayOffset);

                            switch (ret) {
                                case AudioTrack.ERROR_INVALID_OPERATION:
                                    Log.e(TAG, "play fail: ERROR_INVALID_OPERATION");
                                case AudioTrack.ERROR_BAD_VALUE:
                                    Log.e(TAG, "play fail: ERROR_BAD_VALUE");
                                case AudioManager.ERROR_DEAD_OBJECT:
                                    Log.e(TAG, "play fail: ERROR_DEAD_OBJECT");
                                default:
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.w(TAG, "play fail: " + e.getMessage());
                            break;
                        }

                    }
                }

                mState = AudioPlayer.State.STOP;

                mAudioTrack.stop();


                Log.e(TAG, "PlayAudioThread complete...");
            }
        }
    }

}
