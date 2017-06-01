package com.loopeer.android.photodrama4android.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.SeekBar;

import com.loopeer.android.librarys.imagegroupview.utils.FileUtils;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.databinding.ActivityRecordMusicBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.audio.AudioRecorder;
import com.loopeer.android.photodrama4android.media.model.Clip;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ScrollSelectAdapter;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.loopeer.android.photodrama4android.media.model.MusicClip.MIN_RECORD_AUDIO_LENGTH;
import static com.loopeer.android.photodrama4android.media.utils.DateUtils.formatTimeMilli;

public class RecordMusicActivity extends PhotoDramaBaseActivity implements VideoPlayerManager.ProgressChangeListener
        , ScrollSelectView.ClipIndicatorPosChangeListener, ScrollSelectView.ClipSelectedListener, ScrollSelectView.TouchStateListener, VideoPlayerManager.ActualStopListener {

    private ActivityRecordMusicBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private AudioRecorder mAudioRecorder;
    private MusicClip mMusicClipRecording;
    private boolean mIsRecording;
    private MusicClip mSelectedClip;
    private boolean mToolShow = false;
    private boolean mStopValidate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_record_music);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        mAudioRecorder = new AudioRecorder();
        mAudioRecorder.requestPermission(this);
        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, mDrama,
                new SeekWrapper(mBinding.scrollSelectView));
        mVideoPlayerManager.addProgressChangeListener(this);
        mVideoPlayerManager.setFinishTime(mDrama.getShowTimeTotal());
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.seekToVideo(0);
        mVideoPlayerManager.setActualStopListener(this);
        mBinding.glSurfaceView.setOnClickListener(v -> onPlayRectClick(v));
        mBinding.scrollSelectView.setTouchStateListener(this);
        setUpVolumeListener();
        updateBtn();
        updateScrollImageView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
    }

    private void updateBtn() {
        mBinding.btnAdd.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    stopRecordByAction(true);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    stopRecordByAction(false);
                    break;
                case MotionEvent.ACTION_DOWN:
                    startRecord();
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    public void onDeleteClick(View view) {
        if (mSelectedClip != null) {
            mDrama.audioGroup.musicClips.remove(mSelectedClip);
            mVideoPlayerManager.getIMusic().updateDrama(mDrama);
            updateScrollSelectViewClips();
            mSelectedClip = null;
            updateRecordBtnEnable();
            updateBtnView();
        }
    }

    private void stopRecordByAction(boolean validate) {
        mVideoPlayerManager.pauseVideo();
        mStopValidate = validate;
    }

    public void stopActual(long usedTime) {
        mMusicClipRecording.showTime = (int) (usedTime - mMusicClipRecording.startTime);
        mIsRecording = false;
        mBinding.textAdd.setText(R.string.record_add);
        mAudioRecorder.stopRecording();
        mVideoPlayerManager.pauseVideo();
        if (mMusicClipRecording == null) return;
        mMusicClipRecording.setCreateIng(false);
        if (mMusicClipRecording.showTime < MIN_RECORD_AUDIO_LENGTH
                || !mStopValidate) {
            mDrama.audioGroup.musicClips.remove(mMusicClipRecording);
            FileUtils.deleteFile(new File(mMusicClipRecording.path));
        }
        mVideoPlayerManager.getIMusic().updateDrama(mDrama);
        updateScrollSelectViewClips();
        mBinding.scrollSelectView.setStop(true);
        int seekTime = mMusicClipRecording.getEndTime() + 1;
        if (mMusicClipRecording.getEndTime() >= mDrama.getShowTimeTotal()) {
            seekTime = mVideoPlayerManager.getMaxTime();
        }
        mVideoPlayerManager.seekToVideo(seekTime);
        mMusicClipRecording = null;

    }

    private void startRecord() {
        mIsRecording = true;
        mBinding.textAdd.setText(R.string.record_add_finish);
        mMusicClipRecording = new MusicClip((int) mVideoPlayerManager.getGLThread().getUsedTime()
                , MusicClip.MusicType.RECORD_AUDIO);
        mMusicClipRecording.path = FileManager.getInstance().createNewAudioFile();
        mMusicClipRecording.setCreateIng(true);
        if (!checkClipValidate(mMusicClipRecording)) {
            return;
        }
        mDrama.audioGroup.musicClips.add(mMusicClipRecording);
        updateScrollSelectViewClips();
        if (mAudioRecorder.startRecording(mMusicClipRecording)) {
            mVideoPlayerManager.startVideoOnly();
        }
    }

    private void updateScrollSelectViewClips() {
        mBinding.scrollSelectView.updateClips(mDrama.audioGroup.getRecordMusicClips());
    }

    private void updateScrollImageView() {
        mBinding.scrollSelectView.setClipIndicatorPosChangeListener(this);
        mBinding.scrollSelectView.setClipSelectedListener(this);
        mBinding.scrollSelectView.setMinClipShowTime(MIN_RECORD_AUDIO_LENGTH);
        ScrollSelectView.Adapter<TransitionImageWrapper> adapter = new ScrollSelectAdapter();
        mBinding.scrollSelectView.setAdapter(adapter);
        adapter.updateDatas(ClipsCreator.getTransiImageClipsNoEmpty(mDrama.videoGroup));
        updateScrollSelectViewClips();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.menu_done) {
            Analyst.myCreatDubbingSaveClick();
            Intent intent = new Intent();
            intent.putExtra(Navigator.EXTRA_DRAMA, mVideoPlayerManager.getDrama());
            setResult(RESULT_OK, intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mVideoPlayerManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoPlayerManager.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoPlayerManager.onStop();
        mAudioRecorder.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayManagerContainer.getDefault().onFinish(this);
        mVideoPlayerManager.onDestroy();
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {
        mBinding.textStart.setText(formatTimeMilli(progress));
        mBinding.textTotal.setText(formatTimeMilli(maxValue));
    }

    @Override
    public void onProgressStop() {
        mBinding.btnPlayFrame.setSelected(true);
        mBinding.btnPlay.setVisibility(View.VISIBLE);
        mBinding.scrollSelectView.onProgressStop();
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        mBinding.textStart.setText(formatTimeMilli(progress));
        if (mMusicClipRecording != null && mIsRecording) {
            mMusicClipRecording.showTime = (int) (mVideoPlayerManager.getGLThread().getUsedTime() - mMusicClipRecording.startTime);
            mMusicClipRecording.musicSelectedLength = mMusicClipRecording.showTime;
            if (checkClipValidateAndChange(mMusicClipRecording)) {
                stopRecordByAction(true);
            }
        }

        updateRecordBtnEnable();
    }

    private void updateRecordBtnEnable() {
        if (mSelectedClip == null) {
            MusicClip musicClip = new MusicClip((int) mVideoPlayerManager.getGLThread().getUsedTime()
                    , MusicClip.MusicType.RECORD_AUDIO);
            if (!checkClipValidate(musicClip)) {
                mBinding.btnAdd.setEnabled(false);
            } else {
                mBinding.btnAdd.setEnabled(true);
            }
        }
    }

    @Override
    public void onProgressStart() {
        mBinding.btnPlayFrame.setSelected(false);
        mBinding.scrollSelectView.onProgressStart();
        if (mBinding.btnPlay.getVisibility() == View.VISIBLE)
            mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onPlayRectClick(View view) {
        mMusicClipRecording = null;
        if (mVideoPlayerManager.isStop()) {
            mVideoPlayerManager.startVideo();
            mBinding.scrollSelectView.setStop(false);
        } else {
            mVideoPlayerManager.pauseVideo();
            mBinding.scrollSelectView.setStop(true);
        }
    }

    @Override
    public boolean changeTimeByStartIndicator(Clip clip, int offset, int minValue, int maxValue) {
        changeClipTimeByIndicator(clip, offset, maxValue);
        return true;
    }

    @Override
    public boolean changeTimeByEndIndicator(Clip clip, int offset, int minValue, int maxValue) {
        changeClipTimeByIndicator(clip, offset, maxValue);
        return true;
    }

    @Override
    public boolean changeTimeByMiddleLine(Clip clip, int offset, int minValue, int maxValue) {
        changeClipTimeByIndicator(clip, offset, maxValue);
        return true;
    }

    private void changeClipTimeByIndicator(Clip clip, int offset, int maxValue) {
        int preStartTime = clip.startTime;
        clip.startTime += offset;
        if (clip.getEndTime() >= maxValue + 1)
            clip.startTime = maxValue + 1 - clip.showTime;
        if (clip.startTime <= 0) {
            clip.startTime = 0;
        }
        for (MusicClip c : mDrama.audioGroup.getRecordMusicClips()) {
            if (clip != c) {
                if (preStartTime < c.startTime && clip.getEndTime() >= c.startTime) {
                    clip.startTime = c.startTime - 1 - clip.showTime;
                    break;
                }
                if (preStartTime > c.startTime && clip.startTime <= c.getEndTime()) {
                    clip.startTime = c.getEndTime() + 1;
                    break;
                }
            }
        }
    }

    private boolean checkClipValidate(Clip recordingClip) {
        if (recordingClip.startTime + MusicClip.MIN_RECORD_AUDIO_LENGTH > mVideoPlayerManager.getMaxTime())
            return false;
        if (recordingClip.getEndTime() > mVideoPlayerManager.getMaxTime())
            return false;
        for (MusicClip clip : mDrama.audioGroup.getRecordMusicClips()) {
            if (recordingClip != clip) {
                if (recordingClip.startTime < clip.startTime
                        && recordingClip.startTime + MusicClip.MIN_RECORD_AUDIO_LENGTH >= clip.startTime)
                    return false;
                if (recordingClip.startTime < clip.startTime
                        && recordingClip.getEndTime() >= clip.startTime)
                    return false;
            }
        }
        return true;
    }

    private boolean checkClipValidateAndChange(MusicClip musicClip) {
        for (MusicClip clip : mDrama.audioGroup.getRecordMusicClips()) {
            if (musicClip != clip) {
                if (musicClip.startTime < clip.startTime
                        && musicClip.getEndTime() >= clip.startTime) {
                    musicClip.showTime = clip.startTime - musicClip.startTime;
                    musicClip.musicSelectedLength = musicClip.showTime;
                    return true;
                }
            }
        }
        if (musicClip.getEndTime() > mVideoPlayerManager.getMaxTime()) {
            musicClip.showTime = mVideoPlayerManager.getMaxTime() - musicClip.startTime + 1;
            musicClip.musicSelectedLength = musicClip.showTime;
            return true;
        }
        return false;
    }

    @Override
    public void onClipSelected(Clip clip) {
        if (clip != null) {
            mSelectedClip = (MusicClip) clip;
        } else {
            mSelectedClip = null;
        }
        updateBtnView();
    }

    private void updateBtnView() {
        if (mSelectedClip != null) {
            showVolume();
        } else {
            hideVolume();
        }
    }

    private void hideVolume() {
        if (!mToolShow) return;
        mToolShow = false;
        mBinding.switcherBtn.setDisplayedChild(0);
        ObjectAnimator.ofFloat(mBinding.viewMusicVolume.viewVolumeContainer, View.TRANSLATION_Y, 0,
                mBinding.viewMusicVolume.viewVolumeContainer.getHeight()).start();
    }

    private void showVolume() {
        if (mToolShow) return;
        mToolShow = true;
        mBinding.switcherBtn.setDisplayedChild(1);
        mBinding.viewMusicVolume.viewVolumeContainer.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(mBinding.viewMusicVolume.viewVolumeContainer, View.TRANSLATION_Y,
                mBinding.viewMusicVolume.viewVolumeContainer.getHeight(), 0).start();
        mBinding.viewMusicVolume.seekBarVolume.setProgress((int) (mSelectedClip.volume * 100));
        updateVolumeText();
    }

    private void updateVolumeText() {
        mBinding.viewMusicVolume.textVolume.setText(String.format("%2.0f", mSelectedClip.volume * 100) + "%");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAudioRecorder.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStartTouch() {
        mMusicClipRecording = null;
    }

    @Override
    public void onStopTouch() {
        updateBtnView();
    }

    private void setUpVolumeListener() {
        mBinding.viewMusicVolume.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mSelectedClip != null) mSelectedClip.volume = 1f * progress / 100;
                updateVolumeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void actualFinishAt(long usedTime) {
        stopActual(usedTime);
    }
}
