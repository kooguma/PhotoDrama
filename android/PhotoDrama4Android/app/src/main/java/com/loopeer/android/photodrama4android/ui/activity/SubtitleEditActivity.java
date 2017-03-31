package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivitySubtitleEditBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Clip;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.SubtitleClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ScrollSelectAdapter;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;

import static com.loopeer.android.photodrama4android.media.model.SubtitleClip.MIN_SUBTITLE_LENGTH;

public class SubtitleEditActivity extends MovieMakerBaseActivity implements ScrollSelectView.ClipSelectedListener, ScrollSelectView.ClipIndicatorPosChangeListener {

    private ActivitySubtitleEditBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private SubtitleClip mSelectedClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_subtitle_edit);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mBinding.scrollSelectView)
                , mBinding.glSurfaceView, mDrama);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.seekToVideo(0);
        updateScrollImageView();
        updateScrollSelectViewClips();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateScrollImageView() {
        mBinding.scrollSelectView.setClipIndicatorPosChangeListener(this);
        mBinding.scrollSelectView.setClipSelectedListener(this);
        mBinding.scrollSelectView.setMinClipShowTime(MIN_SUBTITLE_LENGTH);
        ScrollSelectView.Adapter<TransitionImageWrapper> adapter = new ScrollSelectAdapter();
        mBinding.scrollSelectView.setAdapter(adapter);
        adapter.updateDatas(ClipsCreator.getTransiImageClipsNoEmpty(mDrama.videoGroup));
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

    public void onTextInputClick(View view) {
        if (mSelectedClip == null && !checkAddValidate())
            return;
        Navigator.startTextInputActivity(this, mSelectedClip == null ? null : mSelectedClip.content);
    }

    public void onDeleteClick(View view) {
        if (mSelectedClip != null) {
            mDrama.videoGroup.subtitleClips.remove(mSelectedClip);
            updateScrollSelectViewClips();
            mVideoPlayerManager.refresh();
        }
    }

    private void updateScrollSelectViewClips() {
        mBinding.scrollSelectView.updateClips(mDrama.videoGroup.subtitleClips);
    }

    private boolean checkAddValidate() {
        SubtitleClip tempClip = new SubtitleClip(
                null
                , (int) mVideoPlayerManager.getGLThread().getUsedTime());
        if (tempClip.startTime + MIN_SUBTITLE_LENGTH > mVideoPlayerManager.getMaxTime())
            return false;
        for (SubtitleClip clip : mDrama.videoGroup.subtitleClips) {
            if (tempClip.startTime < clip.startTime
                    && tempClip.startTime + MIN_SUBTITLE_LENGTH >= clip.startTime)
                return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String content = data.getStringExtra(Navigator.EXTRA_TEXT);
            switch (requestCode) {
                case Navigator.REQUEST_CODE_TEXT_INPUT:
                    if (mSelectedClip == null) {
                        mSelectedClip = new SubtitleClip(
                                content
                                , (int) mVideoPlayerManager.getGLThread().getUsedTime());
                        mDrama.videoGroup.subtitleClips.add(mSelectedClip);
                    } else {
                        mSelectedClip.content = content;
                    }
                    mBinding.scrollSelectView.updateClips(mDrama.videoGroup.subtitleClips);
                    mVideoPlayerManager.requestRender();
                default:
            }
        }
    }

    @Override
    public void onClipSelected(Clip clip) {
        if (clip != null) {
            mSelectedClip = (SubtitleClip) clip;
            mBinding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            mSelectedClip = null;
            mBinding.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean changeTimeByStartIndicator(Clip clip, int offset, int minValue, int maxValue) {
        int preStartTime = clip.startTime;
        int endValue = clip.startTime + clip.showTime;
        if (offset < 0) {
            clip.startTime += offset;
            for (SubtitleClip c : mDrama.videoGroup.subtitleClips) {
                if (clip != c) {
                    if (preStartTime > c.startTime && clip.startTime <= c.getEndTime()) {
                        clip.startTime = c.getEndTime() + 1;
                        break;
                    }
                }
            }
            clip.showTime = endValue - clip.startTime;
        }
        if (offset > 0) {
            clip.startTime += offset;
            clip.showTime = endValue - clip.startTime;
        }
        if (clip.showTime <= minValue) {
            clip.showTime = minValue;
            clip.startTime = endValue - clip.showTime;
        }
        if (clip.startTime <= 0) {
            clip.startTime = 0;
            clip.showTime = endValue;
        }
        return true;
    }

    @Override
    public boolean changeTimeByEndIndicator(Clip clip, int offset, int minValue, int maxValue) {
        clip.showTime += offset;
        if (clip.getEndTime() >= maxValue + 1)
            clip.showTime = maxValue + 1 - clip.startTime;
        if (clip.showTime <= minValue)
            clip.showTime = minValue;

        for (SubtitleClip c : mDrama.videoGroup.subtitleClips) {
            if (clip != c) {
                if (clip.startTime < c.startTime && clip.getEndTime() >= c.startTime) {
                    clip.showTime = c.startTime - clip.startTime;
                    break;
                }
            }
        }
        return true;
    }
}
