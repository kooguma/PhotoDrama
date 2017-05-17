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
import com.loopeer.android.photodrama4android.media.OnSeekProgressChangeListener;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Clip;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.model.SubtitleClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ScrollSelectAdapter;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;
import com.loopeer.android.photodrama4android.ui.widget.SubtitleEditRectView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.loopeer.android.photodrama4android.media.model.SubtitleClip.MIN_SUBTITLE_LENGTH;
import static com.loopeer.android.photodrama4android.media.utils.DateUtils.formatTimeMilli;


public class SubtitleEditActivity extends PhotoDramaBaseActivity implements ScrollSelectView.ClipSelectedListener
        , ScrollSelectView.ClipIndicatorPosChangeListener, VideoPlayerManager.ProgressChangeListener, SubtitleEditRectView.SubtitleRectClickListener
        , ScrollSelectView.TouchStateListener {

    private ActivitySubtitleEditBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private SubtitleClip mSelectedClip;
    private boolean mShowSoftInput;

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
        mBinding.subtitleRectView.setSubtitleRectClickListener(this);
        mBinding.scrollSelectView.setTouchStateListener(this);
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
            mSelectedClip = null;
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
        showTextRect();
        mBinding.scrollSelectView.updateClips(mDrama.videoGroup.subtitleClips);
        mVideoPlayerManager.refreshSubtitleRender();
        mVideoPlayerManager.requestRender();
    }

    public void onInputConfirm(View view) {
        String content = mBinding.textInput.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            updateSubtitle(content);
        }
        hideSoftInputMethod();
        hideInput();
        mBinding.textInput.setText("");
    }

    @Override
    public void onClipSelected(Clip clip) {
        if (clip != null) {
            mSelectedClip = (SubtitleClip) clip;
            mBinding.btnAdd.setEnabled(false);
        } else {
            mSelectedClip = null;
            mBinding.btnAdd.setEnabled(true);
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
    public boolean changeTimeByMiddleLine(Clip clip, int offset, int minValue, int maxValue) {
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
        mBinding.scrollSelectView.onProgressStop();
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        mBinding.textStart.setText(formatTimeMilli(progress));
    }

    @Override
    public void onProgressStart() {
        mBinding.btnPlayFrame.setSelected(false);
        mBinding.scrollSelectView.onProgressStart();
        if (mBinding.btnPlay.getVisibility() == View.VISIBLE)
            mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onPlayRectClick(View view) {
        if (mVideoPlayerManager.isStop()) {
            mVideoPlayerManager.startVideo();
            hideTextRect();
        } else {
            mVideoPlayerManager.pauseVideo();
            showTextRect();
        }
    }


    private void setInputPositionListener() {
        getWindow().getDecorView().getViewTreeObserver()
                .addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    public void onTextInputClick(View view) {
        if (mSelectedClip == null && !checkAddValidate())
            return;
        if (mSelectedClip != null) {
            mBinding.textInput.setText(mSelectedClip.content);
            mBinding.textInput.setSelection(mSelectedClip.content.length());
        }
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
        mShowSoftInput = true;
        registerSubscription(
                Flowable.timer(
                        getResources().getInteger(android.R.integer.config_mediumAnimTime)
                        , TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> {
                            if (mShowSoftInput) mBinding.textInputWrapper.setVisibility(View.VISIBLE);
                        })
        );
    }

    private void hideInput() {
        mShowSoftInput = false;
        mBinding.textInputWrapper.post(() -> mBinding.textInputWrapper.setVisibility(View.INVISIBLE));
    }

    private boolean isSoftKeyboardActive() {
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        return r.bottom < DeviceScreenUtils.getScreenHeight(SubtitleEditActivity.this) / 4 * 3;
    }

    @Override
    public void onRectTextClick() {
        onTextInputClick(null);
    }

    @Override
    public void onRectDeleteClick() {
        onDeleteClick(null);
        hideTextRect();
    }

    @Override
    public void onRectSpaceClick() {
        onPlayRectClick(null);
    }

    private void hideTextRect() {
        mBinding.subtitleRectView.hideTextRect();
    }

    private void showTextRect() {
        if (mSelectedClip == null) {
            mBinding.btnPlay.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnPlay.setVisibility(View.GONE);
        }
        mBinding.subtitleRectView.showTextRect(mSelectedClip);
    }

    @Override
    public void onStartTouch() {
        hideTextRect();
    }

    @Override
    public void onStopTouch() {
        showTextRect();
    }
}
