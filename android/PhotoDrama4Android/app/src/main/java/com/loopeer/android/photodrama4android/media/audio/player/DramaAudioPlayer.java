package com.loopeer.android.photodrama4android.media.audio;

import android.content.Context;
import android.os.AsyncTask;
import com.loopeer.android.photodrama4android.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DramaAudioPlayer {

    private final static int BUFFER_SIZE = 4096;

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
    private AudioPlayer mTalkingPlayer; //人声
    private AudioPlayer mEffectPlayer; //音效

    private Context mContext;

    public DramaAudioPlayer(Context context) {
        mContext = context;

        mBGMPlayer = new AudioPlayer();
        mTalkingPlayer = new AudioPlayer();
        mEffectPlayer = new AudioPlayer();

        try {
            mBGMPlayer.setDataSource(getPCMData(R.raw.music1));
            mTalkingPlayer.setDataSource(getPCMData(R.raw.music2));
            mEffectPlayer.setDataSource(getPCMData(R.raw.music3));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void prepare(){
        mBGMPlayer.prepare();
        mTalkingPlayer.prepare();
        mEffectPlayer.prepare();
    }

    public void play(){
        mBGMPlayer.play();
        mTalkingPlayer.play();
        mEffectPlayer.play();
    }

    public void pause(){
        mBGMPlayer.pause();
        mTalkingPlayer.pause();
        mEffectPlayer.pause();
    }

    public void stop(){
        mBGMPlayer.stop();
        mTalkingPlayer.stop();
        mEffectPlayer.stop();
    }

    public void release(){
        mBGMPlayer.release();
        mTalkingPlayer.release();
        mEffectPlayer.release();
    }

    private byte[] getPCMData(int resId) throws IOException {
        InputStream is = mContext.getResources().openRawResource(resId);
        return inputStreamToByte(is);
    }

    private static byte[] inputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }
        return outStream.toByteArray();
    }

    //解码
    private class DecodingTask extends AsyncTask<String, Integer, Byte[]> {

        @Override protected Byte[] doInBackground(String... params) {
            return new Byte[0];
        }

    }

}
