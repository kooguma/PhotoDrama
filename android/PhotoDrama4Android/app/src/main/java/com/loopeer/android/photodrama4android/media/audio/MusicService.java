package com.loopeer.android.photodrama4android.media.audio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.loopeer.android.photodrama4android.media.model.Drama;

public class MusicService extends Service{
    private MusicBinder mBinder;
    private MusicProcessor mMusicProcessor;
    private Context mContext;
    public MusicService(){
        mBinder = new MusicBinder();
    }

    public void initMusicProcessor(Drama drama, MusicProcessor.ProcessorPrepareListener listener){
        if (mMusicProcessor == null) {
            mMusicProcessor = new MusicProcessor();
        }
        mMusicProcessor.setProcessorPrepareListener(listener);
        mMusicProcessor.updateMusicClipPlayer(mContext, drama.audioGroup.musicClips);
    }

    public void startMusic(){
        mMusicProcessor.startMusic();
    }

    public void pauseMusic(){
        mMusicProcessor.pauseMusic();
    }

    public void seekToMusic(int progress){
        mMusicProcessor.seekToMusic(progress);
    }

    public void releaseMusic(){
        mMusicProcessor.releasePlayer();
    }

    public void onProgressChange(int time) {
        mMusicProcessor.onProgressChange(time);
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
        mMusicProcessor.updateMusicClipPlayer(mContext, drama.audioGroup.musicClips);
    }

    public Context getMContext() {
        return mContext;
    }

    public void updateService(Context context) {
        mContext = context;
    }

    public class MusicBinder extends Binder {
        public MusicService getMusicService(Context context){
            MusicService.this.mContext = context;
            return MusicService.this;
        }
    }

}
