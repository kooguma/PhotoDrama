package com.loopeer.android.photodrama4android.ui.hepler;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.common.util.UriUtil;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.AudioFetchHelper;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.MusicInfoUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getFormatDuration;

public class MediaPlayerWrapper {

    private static final String TAG = "MediaPlayerWrapper";

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private MusicPlayerTask mPlayerTask;
    private Timer mTimer;
    private float mStartPos;
    private float mEndPos;
    private Uri mUri;

    private MusicClipView mMusicClipView;

    private ImageButton mButtonPlay;
    private TextView mTextStart;
    private TextView mTextCur;
    private TextView mTextEnd;

    private int mDuration;

    private HashMap<Uri, PlayState> mHashMap;

    private class PlayState {
        int startPos;
        int curPos;
        int endPos;

        public PlayState(int startPos, int curPos, int endPos) {
            this.startPos = startPos;
            this.curPos = curPos;
            this.endPos = endPos;
        }
    }

    private MusicClipView.IndicatorMoveListener mIndicatorMoveListener
        = new MusicClipView.IndicatorMoveListener() {

        @Override public void onLeftIndicatorMoving(float position1, float position2) {
            mMediaPlayer.pause();
            if (mTextStart != null) {
                final int time = (int) (position1 * mMediaPlayer.getDuration());
                mTextStart.setText(MusicInfoUtils.getFormatDuration(time));
                if (mTextCur != null) {
                    int delta = (int) (Math.abs(position1 - position2) *
                        mMediaPlayer.getDuration());
                    mTextCur.setText(MusicInfoUtils.getFormatDuration(delta));
                }
            }
        }

        @Override public void onRightIndicatorMoving(float position1, float position2) {
            mMediaPlayer.pause();
            if (mTextEnd != null) {
                final int time = (int) (position1 * mMediaPlayer.getDuration());
                mTextEnd.setText(MusicInfoUtils.getFormatDuration(time));
                if (mTextCur != null) {
                    int delta = (int) (Math.abs(position1 - position2) *
                        mMediaPlayer.getDuration());
                    mTextCur.setText(MusicInfoUtils.getFormatDuration(delta));
                }
            }
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
        mHashMap = new HashMap<>();
        setupMediaPlayer();
        mPlayerTask = new MusicPlayerTask();
        mTimer = new Timer();
    }

    private void setupMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(mp -> {
            Log.e(TAG, "onPrepared");
            mStartPos = 0f;
            mEndPos = 1.0f;
            mTimer.schedule(mPlayerTask, 0, 10);
            mp.start();
        });
        mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "error : " + " what = " + what + " extra = " + extra);
            return false;
        });
        mDuration = mMediaPlayer.getDuration();
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

    public void updateController(TextView start, TextView cur, TextView end) {
        mTextStart = start;
        mTextCur = cur;
        mTextEnd = end;
    }

    public void updateController(LinearLayout layoutController) {
        mTextStart = (TextView) layoutController.findViewById(R.id.txt_start);
        mTextCur = (TextView) layoutController.findViewById(R.id.txt_cur);
        mTextEnd = (TextView) layoutController.findViewById(R.id.txt_end);
        mButtonPlay = (ImageButton) layoutController.findViewById(R.id.btn_pause_play_btn);
    }

    public void setupController(String starTime, String curTime, String endTime) {
        if (mTextStart != null) {
            mTextStart.setText(starTime);
        }
        if (mTextCur != null) {
            mTextCur.setText(curTime);
        }
        if (mTextEnd != null) {
            mTextEnd.setText(endTime);
        }
    }

    public void startAsync() {
        mMediaPlayer.prepareAsync();
        mMusicClipView.setIndicatorMoveListener(mIndicatorMoveListener);
    }

    public void start() {
        PlayState state = mHashMap.get(mUri);
        if (state != null) {
            mStartPos = state.startPos;
            mEndPos = state.endPos;
            mTextStart.setText(MusicInfoUtils.getFormatDuration(state.startPos));
            mTextCur.setText(MusicInfoUtils.getFormatDuration(state.curPos));
            mTextEnd.setText(MusicInfoUtils.getFormatDuration(state.endPos));
            mMusicClipView.setDotProgress(state.curPos * 100 / mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(state.curPos);
        } else {
            mMediaPlayer.start();
        }
    }

    public MusicClip generateMusicClip(Voice voice, MusicClip.MusicType type) {
        MusicClip clip = new MusicClip();
        clip.path = type == MusicClip.MusicType.BGM ?
                    FileManager.getInstance().getAudioBgmPath(mContext, voice) :
                    FileManager.getInstance().getAudioEffectPath(mContext, voice);
        clip.startTime = (int) (mStartPos * mMediaPlayer.getDuration());
        clip.showTime = (int) ((mEndPos - mStartPos) * mMediaPlayer.getDuration());
        clip.musicType = type;
        Log.e(TAG, "clip = " + clip.toString());
        return clip;
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public void pause() {
        //save state
        final int duration = mMediaPlayer.getDuration();
        final int startPos = (int) (mStartPos * duration);
        final int endPos = (int) (mEndPos * duration);
        PlayState state = new PlayState(startPos, mMediaPlayer.getCurrentPosition(), endPos);
        mHashMap.put(mUri, state);

        mMediaPlayer.pause();
    }

    public void destroy() {
        mTimer.cancel();
        mPlayerTask.cancel();
        mMediaPlayer.stop();
        mMediaPlayer.release();
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
        mPlayerTask.cancel();
        mTimer.cancel();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    private class MusicPlayerTask extends TimerTask {

        @Override public void run() {
            if (mMediaPlayer == null) return;
            try {
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
                    mTextCur.post(() -> mTextCur.setText(
                        MusicInfoUtils.getFormatDuration(mMediaPlayer.getCurrentPosition())));
                } else {
                    mTextCur.post(() -> mTextCur.setText(
                        MusicInfoUtils.getFormatDuration(mDuration)));
                }
            } catch (IllegalStateException e) {
                return;
            }
        }
    }

}
