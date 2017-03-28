package com.loopeer.android.photodrama4android.media;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MusicBinder mBinder;
    private MediaPlayer mPlayer;//音乐播放器
    private Timer mTimer;//定时器，获取播放进度
    private Activity mContext;
    public MusicService(){
        mBinder = new MusicBinder();
    }

    public void initMediaPlayer(int mMusic){
        mPlayer = MediaPlayer.create(MusicService.this, mMusic);
        setListenner();
        try {
            mPlayer.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setListenner(){
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                reStartMusic();
            }
        });
    }

    public void setPlayProListener(){
        mTimer = new Timer();// 进度监听
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mPlayer != null) {
                            long current = mPlayer.getCurrentPosition();
                            Log.d("qiu", String.valueOf(current));
                        }
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 0, 100);
    }

    public void replaceMusic(int mMusic){
        mPlayer.pause();
        mPlayer = MediaPlayer.create(MusicService.this, mMusic);
        mPlayer.start();
    }

    public void startMusic(){
        if(mPlayer != null) {
            mPlayer.start();
//            setPlayProListener();
        }
    }

    public void reStartMusic(){
        if(mPlayer != null) {
            mPlayer.seekTo(0);
            mPlayer.start();
        }
    }

    public void PauseMusic(){
        if(mPlayer != null) {
            mPlayer.pause();
            if(mTimer != null) {
                mTimer.cancel();
            }
        }
    }

    public void stopMusic(){
        if(mPlayer != null) {
            mPlayer.stop();
        }
    }

    public void seekToMusic(int mSec,int mDuration){
        if(mPlayer != null) {
            mSec = mSec % mDuration;
            mPlayer.seekTo(mSec);
        }
    }

    public void releaseMusic(){
        try {
            if(mPlayer != null){
                mPlayer.stop();
                mPlayer.release();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMusic();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MusicBinder extends Binder {
        public MusicService getMusicService(Activity context){
            MusicService.this.mContext = context;
            return MusicService.this;
        }
    }

}
