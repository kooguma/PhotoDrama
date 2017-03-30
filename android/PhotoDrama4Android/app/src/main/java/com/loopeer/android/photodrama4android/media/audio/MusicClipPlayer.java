package com.loopeer.android.photodrama4android.media.audio;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.io.IOException;

public class MusicClipPlayer implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer;
    private MusicClip mMusicClip;
    private boolean mIsPrepared;
    private MusicClipPlayerLister mMusicClipPlayerLister;

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

    public void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    public void seekToMusic(int progress) {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(mMusicClip.getSeekTime(progress));
        }
    }

    public void startMusic() {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
        }
    }

    public void pauseMusic() {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.pause();
        }
    }

    public void preparePlayer() {
        if (!mIsPrepared) {
            mMediaPlayer.prepareAsync();
        }
    }

    public void onProgressChange(int usedTime) {
        if (usedTime < mMusicClip.startTime || usedTime > mMusicClip.getEndTime()) {
            if (mMediaPlayer.isPlaying()) {
                pauseMusic();
            }
        } else {
            if (!mMediaPlayer.isPlaying()) {
                seekToMusic(usedTime);
                startMusic();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        startMusic();
        pauseMusic();
        mMusicClipPlayerLister.onMusicClipPlayerPrepared(mMusicClip.getKey());
    }

    public interface MusicClipPlayerLister{
        void onMusicClipPlayerPrepared(String key);
    }
}
