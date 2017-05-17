package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.laputapp.utilities.DeviceScreenUtils;
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

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.loopeer.android.photodrama4android.media.model.SubtitleClip.MIN_SUBTITLE_LENGTH;
import static com.loopeer.android.photodrama4android.media.utils.DateUtils.formatTime;
import static com.loopeer.android.photodrama4android.media.utils.DateUtils.formatTimeMilli;

public class SubtitleEditActivity extends PhotoDramaBaseActivity implements ScrollSelectView.ClipSelectedListener
        , ScrollSelectView.ClipIndicatorPosChangeListener, VideoPlayerManager.ProgressChangeListener {

    private ActivitySubtitleEditBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private SubtitleClip mSelectedClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_subtitle_edit);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, mDrama,
                new SeekWrapper(mBinding.scrollSelectView));
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.addProgressChangeListener(this);
        mVideoPlayerManager.seekToVideo(0);
        mBinding.glSurfaceView.setOnClickListener(this::onPlayRectClick);
        updateScrollImageView();
        updateScrollSelectViewClips();
        setInputPositionListener();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
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

        getWindow().getDecorView().getViewTreeObserver()
                .removeOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    public void onPlayClick(View view) {
        mVideoPlayerManager.startVideo();
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
                    updateSubtitle(content);
                default:
            }
        }
    }

    private void updateSubtitle(String content) {
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
    }

    public void onInputConfirm(View view) {
        hideSoftInputMethod();
        hideInput();
        String content = mBinding.textInput.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            updateSubtitle(content);
        }
    }

    @Override
    public void onClipSelected(Clip clip) {
        if (clip != null) {
            mSelectedClip = (SubtitleClip) clip;
//            mBinding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            mSelectedClip = null;
//            mBinding.btnDelete.setVisibility(View.GONE);
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

    @Override
    public void onProgressInit(int progress, int maxValue) {
        mBinding.textStart.setText(formatTimeMilli(progress));
        mBinding.textTotal.setText(formatTimeMilli(maxValue + 1));
    }

    @Override
    public void onProgressStop() {
        mBinding.btnPlayFrame.setSelected(true);
        mBinding.btnPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        mBinding.textStart.setText(formatTimeMilli(progress));
    }

    @Override
    public void onProgressStart() {
        mBinding.btnPlayFrame.setSelected(false);
        if (mBinding.btnPlay.getVisibility() == View.VISIBLE)
            mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onPlayRectClick(View view) {
        if (mVideoPlayerManager.isStop()) {
            mVideoPlayerManager.startVideo();
        } else {
            mVideoPlayerManager.pauseVideo();
        }
    }


    private void setInputPositionListener() {
        getWindow().getDecorView().getViewTreeObserver()
                .addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    public void onTextInputClick(View view) {
        if (mSelectedClip == null && !checkAddValidate())
            return;
        if (isSoftKeyboardActive()) {
            hideSoftInputMethod();
            hideInput();
        } else {
            showSoftInputMethod();
            showInput();
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    int inputPositionY = r.bottom - mBinding.textInputWrapper.getMeasuredHeight();
                    if (isSoftKeyboardActive()) {
                        showInput();
                        mBinding.textInputWrapper.requestFocus();
                        mBinding.textInputWrapper.setY(inputPositionY);
                    } else {
                        hideInput();
                    }
                }
            };


    private void showInput() {
        registerSubscription(
                Flowable.timer(
                        getResources().getInteger(android.R.integer.config_mediumAnimTime)
                        , TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> mBinding.textInputWrapper.setVisibility(View.VISIBLE))
        );
    }

    private void hideInput() {
        mBinding.textInputWrapper.setVisibility(View.INVISIBLE);
    }

    private boolean isSoftKeyboardActive() {
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        return r.bottom < DeviceScreenUtils.getScreenHeight(SubtitleEditActivity.this) / 4 * 3;
    }
}
