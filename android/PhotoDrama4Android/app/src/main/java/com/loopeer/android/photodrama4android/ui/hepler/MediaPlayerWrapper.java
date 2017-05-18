package com.loopeer.android.photodrama4android.ui.hepler;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import com.facebook.common.util.UriUtil;
import com.loopeer.android.photodrama4android.media.utils.AudioFetchHelper;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerWrapper {

    private static final String TAG = "MediaPlayerWrapper";

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private AudioFetchHelper mAudioFetchHelper;
    private MusicPlayerTask mPlayerTask;
    private Timer mTimer;
    private float mStartPos;
    private float mEndPos;
    private boolean mScheduled;
    private Uri mUri;
    private MusicClipView mMusicClipView;
    private MusicClipView.IndicatorMoveListener mIndicatorMoveListener
        = new MusicClipView.IndicatorMoveListener() {

        @Override public void onLeftIndicatorMoving(float position) {
            mMediaPlayer.pause();
        }

        @Override public void onRightIndicatorMoving(float position) {
            mMediaPlayer.pause();
        }

        @Override public void onLeftIndicatorMoved(float position) {
            mMediaPlayer.start();
            mStartPos = position;
            final int mesc = (int) (position * mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(mesc);
        }

        @Override public void onRightIndicatorMoved(float position) {
            mMediaPlayer.start();
            mEndPos = position;
        }
    };

    // private MediaPlayerWrapperListener mListener;
    //
    // public interface MediaPlayerWrapperListener {
    //     void onProgressUpdate(float progress);
    // }

    public MediaPlayerWrapper(Context context) {
        mContext = context;
        mScheduled = false;
        setupMediaPlayer();
    }

    private void setupMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(mp -> {
            mStartPos = 0f;
            mEndPos = 1.0f;
            schedulePlayerTask();
            mp.start();
        });
        mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "error : " + " what = " + what + " extra = " + extra);
            return false;
        });
    }

    private void schedulePlayerTask() {
        mPlayerTask = new MusicPlayerTask();
        mTimer = new Timer();
        mTimer.schedule(mPlayerTask, 0, 10);
    }

    public void updateDataSource(File file) {
        mMediaPlayer.reset();
        try {
            mUri = Uri.fromFile(file);
            mMediaPlayer.setDataSource(mContext, mUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMusicClipView(MusicClipView view) {
        mMusicClipView = view;
    }

    public void startAsync() {
        mMediaPlayer.prepareAsync();
        mMusicClipView.setIndicatorMoveListener(mIndicatorMoveListener);
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void destroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mPlayerTask.cancel();
        mTimer.cancel();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isAlreadyPrepared(File file) {
        if (mUri == null) return false;
        Uri fileUri = Uri.fromFile(file);
        return mUri.equals(fileUri);
    }

    public void reset() {
        mStartPos = 0f;
        mEndPos = 1.0f;
        mScheduled = false;
        mPlayerTask.cancel();
        mTimer.cancel();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    private class MusicPlayerTask extends TimerTask {

        @Override public void run() {
            if (mMediaPlayer.isPlaying()) {
                final int duration = mMediaPlayer.getDuration();
                final int curPosition = mMediaPlayer.getCurrentPosition();
                final float progress = (float) curPosition / duration;
                if (progress >= mEndPos) { //check if end
                    final int mesc = (int) (mStartPos * mMediaPlayer.getDuration());
                    mMediaPlayer.seekTo(mesc);
                } else {
                    mMusicClipView.setDotProgress(progress * 100);
                }
            }
        }
    }

}
