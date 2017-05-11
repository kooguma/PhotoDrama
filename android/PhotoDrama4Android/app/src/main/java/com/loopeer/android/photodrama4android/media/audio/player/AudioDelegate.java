package com.loopeer.android.photodrama4android.media.audio.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.loopeer.android.photodrama4android.media.IMusic;
import com.loopeer.android.photodrama4android.media.audio.MusicService;
import com.loopeer.android.photodrama4android.media.model.Drama;

public class AudioDelegate implements IMusic {

    private AudioService mBindService;
    private boolean mIsBind = false;
    private Context mContext;
    private Drama mDrama;
    private boolean mIsStop;
    private int mUseTime;

    private ServiceConnection mConn;

    public AudioDelegate(Context context, Drama drama, AudioProcessor.AudioProcessorPrepareListener listener) {
        mDrama = drama;
        mContext = context;
        mConn = new ServiceConnection() {
            @Override public void onServiceConnected(ComponentName name, IBinder service) {
                AudioService.AudioBinder mBinder = (AudioService.AudioBinder) service;
                mBindService = mBinder.getMusicService(mContext);
                mBindService.initAudioProcessor(mDrama, listener);
                mBindService.seekToMusic(mUseTime);
                if (!mIsStop) mBindService.startMusic();
                mIsBind = true;
            }

            @Override public void onServiceDisconnected(ComponentName name) {
            }
        };
        bingService();
    }


    private void reConnect(Context context) {
        if (mBindService != null && !context.equals(mBindService.getContext())) {
            mBindService.updateService(context);
        }
    }

    public void bingService() {
        Intent intent = new Intent(mContext, AudioService.class);
        mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        if (mIsBind) {
            mContext.unbindService(mConn);
            mIsBind = false;
        }
    }

    private boolean isBindServiceAvailable() {
        return mBindService != null && mContext.equals(mBindService.getContext());
    }

    @Override public void updateDrama(Drama drama) {
        mDrama = drama;
        if (isBindServiceAvailable())
            mBindService.updateDrama(mDrama);
    }

    @Override public void startMusic() {
        mIsStop = false;
        if (isBindServiceAvailable()) {
            mBindService.startMusic();
        }
    }

    @Override public void seekToMusic(int progress) {
        mUseTime = progress;
        if (isBindServiceAvailable()) {
            mBindService.seekToMusic(progress);
        }
    }

    @Override public void pauseMusic() {
        mIsStop = true;
        if (isBindServiceAvailable()) {
            mBindService.pauseMusic();
        }
    }

    @Override public void stopMusic() {
        mIsStop =true;
        if(isBindServiceAvailable()){
            mBindService.stopMusic();
        }
    }

    @Override public void onProgressChange(int time) {
        if (isBindServiceAvailable()) {
            mBindService.onProgressChange(time);
        }
    }

    @Override public void onResume(Context context, int progress) {
        reConnect(context);
        seekToMusic(progress);
    }

    @Override public void onPause() {
        pauseMusic();
    }

    @Override public void onDestroy() {
        unBindService();
    }
}
