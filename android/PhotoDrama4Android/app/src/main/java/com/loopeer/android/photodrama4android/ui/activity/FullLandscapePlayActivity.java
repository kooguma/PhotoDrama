package com.loopeer.android.photodrama4android.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityFullLandscapePlayBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Drama;

import com.loopeer.android.photodrama4android.model.Theme;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class FullLandscapePlayActivity extends PhotoDramaBaseActivity implements VideoPlayerManager.ProgressChangeListener {

    private ActivityFullLandscapePlayBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private Drama mDrama;
    private Theme mTheme;
    private Subject mHideToolSubject = PublishSubject.create();
    private boolean mToolShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_landscape_play);

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);

        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, mDrama,
                new SeekWrapper(mBinding.seekBar));
        mVideoPlayerManager.addProgressChangeListener(this);
        mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

        mBinding.glSurfaceView.setOnClickListener(v -> {
            if (!mToolShow) {
                showAllBar();
                hideTool();
            } else {
                mVideoPlayerManager.pauseVideo();
            }
        });

        int usedTime = getIntent().getIntExtra(Navigator.EXTRA_USEDTIME, -1);
        boolean restart = getIntent().getBooleanExtra(Navigator.EXTRA_IS_TO_START, false);

        if (usedTime != -1) {
            if (mVideoPlayerManager != null) {
                mVideoPlayerManager.seekToVideo(usedTime);
                if (restart) {
                    mVideoPlayerManager.startVideo();
                }
            }
        } else {
            mVideoPlayerManager.onRestart();
        }

        registerSubscription(
                mHideToolSubject.debounce(1500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(o -> hideAllBar())
                        .subscribe()
        );
        hideTool();
    }

    private void hideTool() {
        mHideToolSubject.onNext(new Object());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        if (mTheme != null) {
            getSupportActionBar().setTitle(mTheme.name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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
    public void onBackPressed() {
        setResultIntent();
        super.onBackPressed();
    }

    private void setResultIntent() {
        final int usedTime = mVideoPlayerManager.getUsedTime();
        final boolean restart = !mVideoPlayerManager.isStop();
        Intent intent = new Intent();
        intent.putExtra(Navigator.EXTRA_USEDTIME, usedTime);
        intent.putExtra(Navigator.EXTRA_IS_TO_START, restart);
        setResult(RESULT_OK, intent);
    }


    @Override
    public void onProgressInit(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.textTimeStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1);
        mBinding.textTimeEnd.setText(hmsTotal);
    }

    @Override
    public void onProgressStop() {
        showAllBar();

        mBinding.btnPlayCenter.setVisibility(View.VISIBLE);
        mBinding.btnPausePlayBtn.setSelected(true);

        //TODO something strange
        new Handler().postDelayed(() -> mBinding.layoutToolBottom.requestLayout(), 30);
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.textTimeStart.setText(hms);
    }

    @Override
    public void onProgressStart() {
        mBinding.btnPausePlayBtn.setSelected(false);
        mBinding.btnPlayCenter.setVisibility(View.GONE);
        hideTool();
    }

    public void onPlayBtnClick(View view) {
        mVideoPlayerManager.startVideo();
    }

    public void onPausePlayBtnClick(View view) {
        if (mVideoPlayerManager.isStop()) {
            mVideoPlayerManager.startVideo();
        } else {
            mVideoPlayerManager.pauseVideo();
        }
    }

    private void hideAllBar() {
        if (mVideoPlayerManager.getGLThread().isStop() || !mToolShow) return;
        mToolShow = false;
        ObjectAnimator.ofFloat(mBinding.layoutToolBottom, View.TRANSLATION_Y, 0, mBinding.layoutToolBottom.getHeight()).start();
        ObjectAnimator.ofFloat(mBinding.layoutToolTop, View.TRANSLATION_Y, 0, -mBinding.layoutToolTop.getHeight()).start();
    }

    private void showAllBar() {
        if (mToolShow) return;
        mToolShow = true;
        ObjectAnimator.ofFloat(mBinding.layoutToolTop, View.TRANSLATION_Y, -mBinding.layoutToolTop.getHeight(), 0).start();
        ObjectAnimator.ofFloat(mBinding.layoutToolBottom, View.TRANSLATION_Y, mBinding.layoutToolBottom.getHeight(), 0).start();
    }

}
