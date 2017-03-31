package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
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

public class SoundEffectActivity extends MovieMakerBaseActivity
        implements ScrollSelectView.ClipIndicatorPosChangeListener, ScrollSelectView.ClipSelectedListener {

    private ActivitySoundEffectBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private MusicClip mSelectedClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sound_effect);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mBinding.scrollSelectView)
                , mBinding.glSurfaceView, mDrama);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.seekToVideo(0);
        updateScrollImageView();
    }

    public void onMusicClick(View view) {
        MusicClip musicClip = new MusicClip((int) mVideoPlayerManager.getGLThread().getUsedTime()
                , MusicClip.MusicType.SOUND_EFFECT);
        if (!checkClipValidate(musicClip)) {
            return;
        }
        Navigator.startTestMusicSelectedActivity(this, mDrama, REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT);
    }

    public void onDeleteClick(View view) {
        if (mSelectedClip != null) {
            mDrama.audioGroup.musicClips.remove(mSelectedClip);
            mVideoPlayerManager.getIMusic().updateDrama(mDrama);
            updateScrollSelectViewClips();
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

    public void onPlayClick(View view) {
        mVideoPlayerManager.startVideo();
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

    @Override
    public void onClipSelected(Clip clip) {
        if (clip != null) {
            mSelectedClip = (MusicClip) clip;
            mBinding.switcherBtn.setDisplayedChild(1);
        } else {
            mSelectedClip = null;
            mBinding.switcherBtn.setDisplayedChild(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            MusicClip musicClip = (MusicClip) data.getSerializableExtra(Navigator.EXTRA_MUSIC_CLIP);
            switch (requestCode) {
                case REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT:
                    if (musicClip != null) {
                        mDrama.audioGroup.musicClips.add(musicClip);
                        mVideoPlayerManager.getIMusic().updateDrama(mDrama);
                        updateScrollSelectViewClips();
                    }
                default:
            }
        }
    }
}