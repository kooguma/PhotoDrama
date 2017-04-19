package com.loopeer.android.photodrama4android.media.audio;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.IntDef;

import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MusicClipPlayer implements MediaPlayer.OnPreparedListener {

    private final static int PREPARED_STATE_NO = 0; 
    private final static int PREPARED_STATE_ING = 1;
    private final static int PREPARED_STATE_ED = 2;
    @IntDef({
            PREPARED_STATE_NO,
            PREPARED_STATE_ING,
            PREPARED_STATE_ED,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaPreparedState {}

    private MediaPlayer mMediaPlayer;
    private MusicClip mMusicClip;
    private MusicClipPlayerLister mMusicClipPlayerLister;
    private boolean mIsPause;
    private @MediaPreparedState int mPreparedState;
    
    public MusicClipPlayer(Context context, MusicClip clip, MusicClipPlayerLister listener) {
        mMusicClip = clip;
        mMusicClipPlayerLister = listener;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        try {
            mMediaPlayer.setDataSource(mMusicClip.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Context context, MusicClip clip, MusicClipPlayerLister listener) {
        mMusicClip = clip;
        mMusicClipPlayerLister = listener;
        if (isPrepared() && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mMediaPlayer.reset();
        mPreparedState = PREPARED_STATE_NO;
        try {
            mMediaPlayer.setDataSource(mMusicClip.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPrepared() {
        return mPreparedState == PREPARED_STATE_ED;
    }

    public boolean isNotPrepare() {
        return mPreparedState == PREPARED_STATE_NO;
    }

    public void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    public void seekToMusic(int progress) {
        if (mMediaPlayer != null && isPrepared()) {
            mMediaPlayer.seekTo(mMusicClip.getSeekTime(progress));
        }
    }

    public void startMusic() {
        mIsPause = false;
        if (mMediaPlayer != null && isPrepared()) {
            mMediaPlayer.start();
        }
    }

    public void pauseMusic() {
        mIsPause = true;
        if (mMediaPlayer != null && isPrepared()) {
            mMediaPlayer.pause();
        }
    }

    public void preparePlayer() {
        if (isNotPrepare()) {
            mMediaPlayer.prepareAsync();
            mPreparedState = PREPARED_STATE_ING;
        }
    }

    public void onProgressChange(int usedTime) {
        if (usedTime < mMusicClip.startTime || usedTime > mMusicClip.getEndTime()) {
            if (mMediaPlayer.isPlaying()) {
                if (mMediaPlayer != null && isPrepared()) {
                    mMediaPlayer.pause();
                }
            }
        } else {
            if (!mMediaPlayer.isPlaying() && !mIsPause) {
                seekToMusic(usedTime);
                if (mMediaPlayer != null && isPrepared()) {
                    mMediaPlayer.start();
                }
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPreparedState = PREPARED_STATE_ED;
        mMediaPlayer.start();
        mMediaPlayer.pause();
        mMusicClipPlayerLister.onMusicClipPlayerPrepared(mMusicClip.getKey());
    }

    public interface MusicClipPlayerLister{
        void onMusicClipPlayerPrepared(String key);
    }
}
