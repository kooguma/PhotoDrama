package com.loopeer.android.photodrama4android.ui.hepler;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
    private AppCompatSeekBar mSeekBar;

    private ImageButton mButtonPlay;
    private TextView mTextStart;
    private TextView mTextCur;
    private TextView mTextEnd;

    private int mDuration;

    private HashMap<Uri, PlayState> mHashMap;

    private class PlayState {
        float startPos;
        float curPos;
        float endPos;

        public PlayState(float startPos, float curPos, float endPos) {
            this.startPos = startPos;
            this.curPos = curPos;
            this.endPos = endPos;
        }

        @Override public String toString() {
            return "PlayState{" +
                "startPos=" + startPos +
                ", curPos=" + curPos +
                ", endPos=" + endPos +
                '}';
        }
    }

    private MusicClipView.IndicatorMoveListener mIndicatorMoveListener
        = new MusicClipView.IndicatorMoveListener() {

        @Override public void onLeftIndicatorMoving(float position1, float position2) {
            mMediaPlayer.pause();
            if (mTextStart != null) {
                final int time = (int) (position1 * mMediaPlayer.getDuration());
                mTextStart.setText(getFormatDuration(time));
                if (mTextCur != null) {
                    int delta = (int) (Math.abs(position1 - position2) *
                        mMediaPlayer.getDuration());
                    mTextCur.setText(getFormatDuration(delta));
                }
            }
        }

        @Override public void onRightIndicatorMoving(float position1, float position2) {
            mMediaPlayer.pause();
            if (mTextEnd != null) {
                final int time = (int) (position1 * mMediaPlayer.getDuration());
                mTextEnd.setText(getFormatDuration(time));
                if (mTextCur != null) {
                    int delta = (int) (Math.abs(position1 - position2) *
                        mMediaPlayer.getDuration());
                    mTextCur.setText(getFormatDuration(delta));
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
            final int mesc = (int) (mStartPos * mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(mesc);
        }
    };

    private boolean isTrackManual;

    private AppCompatSeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener
        = new SeekBar.OnSeekBarChangeListener() {
        @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isTrackManual) {
                mMediaPlayer.pause();
            }
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) {
            isTrackManual = true;
        }

        @Override public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaPlayer.start();
            isTrackManual = false;
            mStartPos = (float) seekBar.getProgress() / 100;
            final int mesc = (int) (mStartPos * mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(mesc);
        }
    };

    public MediaPlayerWrapper(Context context) {
        mContext = context;
        mHashMap = new HashMap<>();
        setupMediaPlayer();
    }

    private void setupMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(mp -> {
            Log.e(TAG, "onPrepared");
            if (!restoreState()) {
                mStartPos = 0f;
                mEndPos = 1.0f;
            }
            scheduleTask();
            mp.start();
        });
        mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "error : " + " what = " + what + " extra = " + extra);
            return false;
        });
        mDuration = mMediaPlayer.getDuration();
    }

    private void scheduleTask() {
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

    public void updateSeekBar(AppCompatSeekBar seekBar) {
        mSeekBar = seekBar;
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
        if (mMusicClipView != null) {
            mMusicClipView.setIndicatorMoveListener(mIndicatorMoveListener);
        }
        if (mSeekBar != null) {
            mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        }
    }

    public void start() {
        if (!restoreState()) {
            mMediaPlayer.start();
        }
    }

    private boolean restoreState() {
        PlayState state = mHashMap.get(mUri);
        if (state != null) {
            final int duration = mMediaPlayer.getDuration();
            mStartPos = state.startPos;
            mEndPos = state.endPos;
            mTextStart.setText(getFormatDuration((int) (mStartPos * duration)));
            mTextEnd.setText(getFormatDuration((int) (mEndPos * duration)));
            if (mMusicClipView != null) {
                mMusicClipView.setDotProgress(state.curPos * 100);
            }
            if (mSeekBar != null) {
                // TODO: 2017/5/20
            }
            mMediaPlayer.seekTo((int) (state.curPos * mMediaPlayer.getDuration()));
            mMediaPlayer.start();
            return true;
        } else {
            return false;
        }
    }

    public MusicClip generateMusicClip(Voice voice, MusicClip.MusicType type) {
        MusicClip clip = new MusicClip();
        if (type == MusicClip.MusicType.BGM) {
            clip.path = FileManager.getInstance().getAudioBgmPath(mContext, voice);
            clip.musicStartOffset = (int) (mStartPos * mMediaPlayer.getDuration());
            clip.musicSelectedLength = (int) ((mEndPos - mStartPos) * mMediaPlayer.getDuration());
            clip.showTime = clip.musicSelectedLength;
        } else {
            clip.path = FileManager.getInstance().getAudioEffectPath(mContext, voice);
            clip.musicStartOffset = 0;
            clip.musicSelectedLength = mMediaPlayer.getDuration();
            clip.showTime = clip.musicSelectedLength;
        }
        clip.musicType = type;
        Log.e(TAG, "clip = " + clip.toString());
        return clip;
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public void pause() {
        //save state
        final float curPos = (float) mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration();
        PlayState state = new PlayState(mStartPos, curPos, mEndPos);
        mHashMap.put(mUri, state);
        mMediaPlayer.pause();
        Log.e(TAG, "state saved ! " + state.toString());
    }

    public void destroy() {
        mHashMap.clear();
        mHashMap = null;
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mPlayerTask != null) {
            mPlayerTask.cancel();
        }
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
                        Log.e(TAG, "mesc = " + mesc);
                        mMediaPlayer.seekTo(mesc);
                    } else {
                        if (mMusicClipView != null) {
                            mMusicClipView.setDotProgress(progress * 100);
                        }
                        if (mSeekBar != null) {
                            mSeekBar.setProgress((int) (progress * 100));
                        }
                    }

                    // TODO: 2017/5/20  curPosition 取不到 duration
                    if (duration - curPosition <= 900) {
                        mTextCur.post(() -> mTextCur.setText(
                            getFormatDuration(duration)));
                    } else {
                        mTextCur.post(() -> mTextCur.setText(
                            getFormatDuration(curPosition)));
                    }
                }
            } catch (IllegalStateException e) {
                return;
            }
        }
    }

}
