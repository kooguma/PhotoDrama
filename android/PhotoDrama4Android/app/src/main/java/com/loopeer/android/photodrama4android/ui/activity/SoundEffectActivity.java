package com.loopeer.android.photodrama4android.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.databinding.ActivitySoundEffectBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Clip;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ScrollSelectAdapter;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;

import static com.loopeer.android.photodrama4android.Navigator.REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT;
import static com.loopeer.android.photodrama4android.media.model.MusicClip.MIN_SOUND_EFFECT_LENGTH;
import static com.loopeer.android.photodrama4android.media.utils.DateUtils.formatTimeMilli;

public class SoundEffectActivity extends PhotoDramaBaseActivity
        implements ScrollSelectView.ClipIndicatorPosChangeListener, ScrollSelectView.ClipSelectedListener, ScrollSelectView.TouchStateListener, VideoPlayerManager.ProgressChangeListener {

    private ActivitySoundEffectBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private MusicClip mSelectedClip;
    private boolean mToolShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sound_effect);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, mDrama,
                new SeekWrapper(mBinding.scrollSelectView));
        mVideoPlayerManager.addProgressChangeListener(this);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.seekToVideo(0);
        mBinding.glSurfaceView.setOnClickListener(v -> onPlayRectClick(v));
        mBinding.scrollSelectView.setTouchStateListener(this);
        setUpVolumeListener();
        updateScrollImageView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
    }

    public void onMusicClick(View view) {
        Analyst.myCreatSoundEffectAddClick();
        MusicClip musicClip = new MusicClip((int) mVideoPlayerManager.getGLThread().getUsedTime()
                , MusicClip.MusicType.SOUND_EFFECT);
        if (!checkClipValidate(musicClip)) {
            return;
        }
        Navigator.addAddMusicActivity(this, MusicClip.MusicType.SOUND_EFFECT);
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

    private void updateRecordBtnEnable() {
        if (mSelectedClip == null) {
            MusicClip musicClip = new MusicClip((int) mVideoPlayerManager.getGLThread().getUsedTime()
                    , MusicClip.MusicType.SOUND_EFFECT);
            if (!checkClipValidate(musicClip)) {
                mBinding.btnAdd.setEnabled(false);
            } else {
                mBinding.btnAdd.setEnabled(true);
            }
        }
    }

    private void updateScrollSelectViewClips() {
        mBinding.scrollSelectView.updateClips(mDrama.audioGroup.getSoundEffectClips());
    }

    private void updateScrollImageView() {
        mBinding.scrollSelectView.setClipIndicatorPosChangeListener(this);
        mBinding.scrollSelectView.setClipSelectedListener(this);
        mBinding.scrollSelectView.setMinClipShowTime(MIN_SOUND_EFFECT_LENGTH);
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
            Analyst.myCreatSoundEffectSaveClick();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayManagerContainer.getDefault().onFinish(this);
        mVideoPlayerManager.onDestroy();
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
        for (MusicClip c : mDrama.audioGroup.getSoundEffectClips()) {
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
        if (recordingClip.startTime + MIN_SOUND_EFFECT_LENGTH > mVideoPlayerManager.getMaxTime())
            return false;
        if (recordingClip.getEndTime() > mVideoPlayerManager.getMaxTime())
            return false;
        for (MusicClip clip : mDrama.audioGroup.getSoundEffectClips()) {
            if (recordingClip != clip) {
                if (recordingClip.startTime < clip.startTime
                        && recordingClip.startTime + MIN_SOUND_EFFECT_LENGTH >= clip.startTime)
                    return false;
                if (recordingClip.startTime < clip.startTime
                        && recordingClip.getEndTime() >= clip.startTime)
                    return false;
            }
        }
        return true;
    }

    private void checkClipValidateAndChange(MusicClip musicClip) {
        for (MusicClip clip : mDrama.audioGroup.getSoundEffectClips()) {
            if (musicClip != clip) {
                if (musicClip.startTime < clip.startTime
                        && musicClip.getEndTime() >= clip.startTime) {
                    musicClip.showTime = clip.startTime - musicClip.startTime;
                    musicClip.musicSelectedLength = musicClip.showTime;
                    return;
                }
            }
        }
        if (musicClip.getEndTime() > mVideoPlayerManager.getMaxTime()) {
            musicClip.showTime = mVideoPlayerManager.getMaxTime() - musicClip.startTime;
            musicClip.musicSelectedLength = musicClip.showTime;
            return;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            MusicClip musicClip = (MusicClip) data.getSerializableExtra(Navigator.EXTRA_MUSIC_CLIP);
            switch (requestCode) {
                case REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT:
                    if (musicClip != null) {
                        musicClip.startTime = mVideoPlayerManager.getUsedTime();
                        checkClipValidateAndChange(musicClip);
                        mDrama.audioGroup.musicClips.add(musicClip);
                        mVideoPlayerManager.getIMusic().updateDrama(mDrama);
                        mSelectedClip = musicClip;
                        showVolume();
                        updateScrollSelectViewClips();
                    }
                default:
            }
        }
    }

    public void onPlayRectClick(View view) {
        if (mVideoPlayerManager.isStop()) {
            mVideoPlayerManager.startVideo();
            mBinding.scrollSelectView.setStop(false);
        } else {
            mVideoPlayerManager.pauseVideo();
            mBinding.scrollSelectView.setStop(true);
        }
    }

    @Override
    public void onStartTouch() {

    }

    @Override
    public void onStopTouch() {

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

    private void updateBtnView() {
        if (mSelectedClip != null) {
            showVolume();
        } else {
            hideVolume();
        }
    }

    private void hideVolume() {
        if (!mToolShow) return;
        mBinding.switcherBtn.setDisplayedChild(0);
        mToolShow = false;
        ObjectAnimator.ofFloat(mBinding.viewMusicVolume.viewVolumeContainer, View.TRANSLATION_Y, 0,
                mBinding.viewMusicVolume.viewVolumeContainer.getHeight()).start();
    }

    private void showVolume() {
        if (mToolShow) return;
        mBinding.switcherBtn.setDisplayedChild(1);
        mBinding.viewMusicVolume.viewVolumeContainer.setVisibility(View.VISIBLE);
        mToolShow = true;
        ObjectAnimator.ofFloat(mBinding.viewMusicVolume.viewVolumeContainer, View.TRANSLATION_Y,
                mBinding.viewMusicVolume.viewVolumeContainer.getHeight(), 0).start();
        mBinding.viewMusicVolume.seekBarVolume.setProgress((int) (mSelectedClip.volume * 100));
        updateVolumeText();
    }

    private void updateVolumeText() {
        mBinding.viewMusicVolume.textVolume.setText(String.format("%2.0f", mSelectedClip.volume * 100) + "%");
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
        if (mSelectedClip == null) {
            MusicClip musicClip = new MusicClip((int) mVideoPlayerManager.getGLThread().getUsedTime()
                    , MusicClip.MusicType.RECORD_AUDIO);
            if (!checkClipValidate(musicClip)) {
                mBinding.btnAdd.setEnabled(false);
            } else {
                mBinding.btnAdd.setEnabled(true);
            }
        }
        updateBtnView();
    }

    @Override
    public void onProgressStart() {
        mBinding.btnPlayFrame.setSelected(false);
        mBinding.scrollSelectView.onProgressStart();
        if (mBinding.btnPlay.getVisibility() == View.VISIBLE)
            mBinding.btnPlay.setVisibility(View.GONE);
    }
}
