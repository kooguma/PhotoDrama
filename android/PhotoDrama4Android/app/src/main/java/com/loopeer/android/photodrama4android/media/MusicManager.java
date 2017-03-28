package com.loopeer.android.photodrama4android.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import com.loopeer.android.photodrama4android.R;
import java.io.IOException;

public class MusicManager implements IMusic, MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener {

    private static final String TAG = "MusicManager";

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private long mDuration;
    private boolean mIsPrepared;
    private long prepareStart;

    public MusicManager(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        try {
            // TODO: 2017/3/27 test
            AssetFileDescriptor afd = mContext.getResources()
                .openRawResourceFd(R.raw.music_new_york_love);
            mMediaPlayer.setDataSource(afd.getFileDescriptor());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            prepareStart = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error: " + e.getMessage());
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setLooping(false);
    }

    @Override
    public void startMusic() {
        // if (mIsPrepared) {
        //     mMediaPlayer.start();
        // } else {
        //     Log.e(TAG, "startMusic : " + "MediaPlayer hasn't been prepared");
        // }
        mMediaPlayer.start();
    }

    @Override
    public void seekToMusic(int progress, int max) {
        if (mIsPrepared) {
            int msec = (int) ((float) (progress / max) * mDuration);
            mMediaPlayer.seekTo(msec);
        } else {
            Log.e(TAG, "seekToMusic : " + "MediaPlayer hasn't been prepared");
        }
    }

    @Override
    public void pauseMusic() {
        mMediaPlayer.stop();
    }

    @Override public void onDestroy() {
        mMediaPlayer.release();
    }

    @Override public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        mDuration = mMediaPlayer.getDuration();
        Log.e(TAG, "prepare time = " + (System.currentTimeMillis() - prepareStart));
        mMediaPlayer.start();
        Log.e(TAG, "duration = " + mDuration);
    }

    @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError : " + " what = " + what + " extra = " + extra);
        return false;
    }

}
