package com.loopeer.android.photodrama4android.media.audio;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.io.File;
import java.io.IOException;

public class MusicClipPlayer {

    private MediaPlayer mMediaPlayer;
    private MusicClip mMusicClip;

    public MusicClipPlayer(Context context, MusicClip clip, MediaPlayer.OnPreparedListener listener) {
        mMusicClip = clip;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(listener);
        Uri uri = Uri.fromFile(new File(mMusicClip.path));
        try {
            mMediaPlayer.setDataSource(context.getApplicationContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    public void seekToMusic(int progress) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(mMusicClip.getSeekTime(progress));
        }
    }

    public void startMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void pauseMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void preparePlayer() {
        mMediaPlayer.prepareAsync();
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
}
