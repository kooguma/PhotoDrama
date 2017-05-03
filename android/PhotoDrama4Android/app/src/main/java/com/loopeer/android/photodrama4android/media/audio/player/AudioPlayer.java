package com.loopeer.android.photodrama4android.media.audio;

import android.media.AudioTrack;
import android.util.Log;
import com.loopeer.android.photodrama4android.media.OnSeekProgressChangeListener;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    private AudioTrack mAudioTrack;                          // AudioTrack
    private AudioParams mAudioParams;                          // Attributes

    private byte[] mBytes;                                   // 音频数据
    private int mPrimePlaySize = 0;                          // 较优播放块大小
    private int mPlayOffset = 0;                             // 当前播放位置

    private boolean mThreadExitFlag;                         // 线程退出标志
    private WorkThread mWorkThread;                          // 工作线程

    private int mTotalTime;                                  // 总时间

    private AudioTrack.OnPlaybackPositionUpdateListener mPlaybackPositionUpdateListener;

    private SeekWrapper mSeekWrapper;

    public AudioPlayer() {
    }

    public void setAttributes(AudioParams attributes) {
        this.mAudioParams = attributes;
    }

    public void setDataSource(byte[] bytes) {
        this.mBytes = bytes;
    }

    /*public void setDataSource(String path) throws IOException {
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        mBytes = new byte[(int) file.length()];
        inputStream.read(mBytes);
    }*/

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

        //
        mTotalTime = mBytes.length / mAudioParams.mParams.mFrequency / 4;

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

        //seekwrapper
    }

    public void seekTo(int time) {
        mAudioTrack.pause();
        float p = time / mTotalTime;
        mPlayOffset = (int) (p * mBytes.length);
        mAudioTrack.play();
    }

    public void play() {
        mPlayOffset = 0;
        startWorkThread();
    }

    public void pause() {
        mAudioTrack.pause();
        stopWorkThread();
    }

    public void stop() {
        mAudioTrack.stop();
        stopWorkThread();
    }

    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public long getTotalTime() {
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

    class WorkThread extends Thread {

        private static final String TAG = "PlayAudioThread";

        @Override public void run() {

            mAudioTrack.play();

            while (!mThreadExitFlag) {
                try {

                    int size = mAudioTrack.write(mBytes, mPlayOffset, mPrimePlaySize);
                    mPlayOffset += mPrimePlaySize;
                    Log.e(TAG, "run playOffset = " + mPlayOffset);
                } catch (Exception e) {
                    e.printStackTrace();
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
