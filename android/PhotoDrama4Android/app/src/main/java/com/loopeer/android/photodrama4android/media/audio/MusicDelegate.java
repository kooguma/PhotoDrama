package com.loopeer.android.photodrama4android.media.audio;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.loopeer.android.photodrama4android.media.IMusic;
import com.loopeer.android.photodrama4android.media.model.Drama;

public class MusicDelegate implements IMusic {

    private MusicService mBindService;
    private boolean mIsBind = false;
    private Context mContext;
    private Drama mDrama;

    private ServiceConnection mConn;

    public MusicDelegate(Context context, Drama drama, MusicProcessor.ProcessorPrepareListener listener) {
        mDrama = drama;
        mContext = context;
        mConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder mBinder = (MusicService.MusicBinder) service;
                mBindService = mBinder.getMusicService(mContext);
                mBindService.initMusicProcessor(mDrama, listener);
                mIsBind = true;
            }
        };
        bingService();
    }

    public void bingService() {
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }


    public void unBindService() {
        if (mIsBind) {
            mContext.unbindService(mConn);
            mIsBind = false;
        }
    }

    public void updateDrama(Drama drama) {
        mDrama = drama;
        if (mBindService != null)
            mBindService.updateDrama(mDrama);
    }

    @Override
    public void startMusic() {
        if (mBindService != null) {
            mBindService.startMusic();
        }
    }

    @Override
    public void seekToMusic(int progress) {
        if (mBindService != null) {
            mBindService.seekToMusic(progress);
        }
    }

    @Override
    public void pauseMusic() {
        if (mBindService != null) {
            mBindService.pauseMusic();
        }
    }

    @Override
    public void onProgressChange(int time) {
        if (mBindService != null) {
            mBindService.onProgressChange(time);
        }
    }

    @Override
    public void onPause() {
        pauseMusic();
    }

    @Override
    public void onDestroy() {
        unBindService();
    }
}
