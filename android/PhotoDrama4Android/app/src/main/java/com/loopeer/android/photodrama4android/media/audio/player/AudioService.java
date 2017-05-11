package com.loopeer.android.photodrama4android.media.audio.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.loopeer.android.photodrama4android.media.model.Drama;

public class AudioService extends Service {

    private AudioService.AudioBinder mBinder;
    private AudioProcessor mAudioProcessor;
    private Context mContext;

    public AudioService() {
        mBinder = new AudioBinder();
    }

    public void initAudioProcessor(Drama drama, AudioProcessor.AudioProcessorPrepareListener listener) {
        if (mAudioProcessor == null) {
            mAudioProcessor = new AudioProcessor(mContext);
        }
        mAudioProcessor.setProcessorPrepareListener(listener);
        mAudioProcessor.updateAudioClipPlayer(mContext,drama.audioGroup.musicClips);
    }

    public void startMusic() {
        mAudioProcessor.startMusic();
    }

    public void pauseMusic() {
        mAudioProcessor.pauseMusic();
    }

    public void stopMusic(){
        mAudioProcessor.stopMusic();
    }

    public void seekToMusic(int progress) {
        mAudioProcessor.seekToMusic(progress);
    }

    public void releaseMusic() {
        mAudioProcessor.releasePlayer();
    }

    public void onProgressChange(int time) {
        mAudioProcessor.onProgressChange(time);
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

    public void updateDrama(Drama drama) {
        mAudioProcessor.updateAudioClipPlayer(mContext,drama.audioGroup.musicClips);
    }

    public Context getContext() {
        return mContext;
    }

    public void updateService(Context context) {
        mContext = context;
    }

    public class AudioBinder extends Binder {

        public AudioService getMusicService(Context context) {
            AudioService.this.mContext = context;
            return AudioService.this;
        }
    }

}
