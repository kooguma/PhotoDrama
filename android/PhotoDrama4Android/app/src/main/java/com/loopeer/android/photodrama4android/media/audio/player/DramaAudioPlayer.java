package com.loopeer.android.photodrama4android.media.audio.player;

import android.content.Context;
import android.os.AsyncTask;
import java.io.IOException;

public class DramaAudioPlayer {

    private static final String TAG = "DramaAudioPlayer";

    public static class State {
        public static final int UNPREPARED = 0; //未就绪
        public static final int PREPARED = 1; //已经就绪
        public static final int PLAYING = 2; //播放中
        public static final int PAUSE = 3; //暂停
    }

    private int mState;

    //三个音轨
    private AudioPlayer mBGMPlayer; //背景音乐
    private AudioPlayer mRecordPlayer; //人声
    private AudioPlayer mEffectPlayer; //音效

    private Context mContext;

    public DramaAudioPlayer(Context context) {
        mContext = context;
        mBGMPlayer = new AudioPlayer();
        mRecordPlayer = new AudioPlayer();
        mEffectPlayer = new AudioPlayer();
    }

    // public void setBGMDataSource(String path) throws IOException {
    //     mBGMPlayer.setDataSource(path);
    // }
    //
    // public void setRecordDataSource(String path) throws IOException {
    //     mRecordPlayer.setDataSource(path);
    //
    // }
    //
    // public void setEffectPlayer(String path) throws IOException {
    //     mEffectPlayer.setDataSource(path);
    //
    // }

    public int getTotalTime() {
        if (mBGMPlayer == null || mRecordPlayer == null || mEffectPlayer == null) {
            return 0;
        } else {
            final int max1 = Math.max(mBGMPlayer.getTotalTime(), mRecordPlayer.getTotalTime());
            final int max2 = Math.max(max1, mEffectPlayer.getTotalTime());
            return Math.max(max1, max2);
        }
    }

    public void seekTo(int time) {
        mBGMPlayer.seekTo(time);
        mRecordPlayer.seekTo(time);
        mEffectPlayer.seekTo(time);
    }

    /**
     * must be called after setDataSource
     */
    public void prepare() {
        mBGMPlayer.prepare();
        mRecordPlayer.prepare();
        mEffectPlayer.prepare();
    }

    public void play() {
        mBGMPlayer.play();
        mRecordPlayer.play();
        mEffectPlayer.play();
    }

    public void pause() {
        mBGMPlayer.pause();
        mRecordPlayer.pause();
        mEffectPlayer.pause();
    }

    public void stop() {
        mBGMPlayer.stop();
        mRecordPlayer.stop();
        mEffectPlayer.stop();
    }

    public void release() {
        mBGMPlayer.release();
        mRecordPlayer.release();
        mEffectPlayer.release();
    }

    //解码
    private class DecodingTask extends AsyncTask<String, Integer, Byte[]> {

        @Override protected Byte[] doInBackground(String... params) {
            return new Byte[0];
        }

    }

}
