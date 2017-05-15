package com.loopeer.android.photodrama4android.ui.hepler;

import android.animation.ObjectAnimator;
import android.view.MenuItem;
import android.view.View;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaEditBinding;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.ui.activity.DramaEditActivity;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.loopeer.android.photodrama4android.ui.hepler.FullBottomLayoutHelper.updateBottomLayoutPadding;

public class DramaEditOrientationAdapter extends OrientationAdapter<ActivityDramaEditBinding, DramaEditActivity> implements VideoPlayerManager.ProgressChangeListener {

    private VideoPlayerManager mVideoPlayerManager;
    private boolean mIsLandscape;
    private boolean mToolShow = true;
    private Subject mHideToolSubject = PublishSubject.create();

    public DramaEditOrientationAdapter(ActivityDramaEditBinding activityDataBinding, DramaEditActivity activity) {
        super(activityDataBinding, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerSubscription(
                mHideToolSubject.debounce(mActivity.getResources().getInteger(R.integer.movie_show_time), TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(o -> hideAllBar())
                        .subscribe()
        );
        mBinding.glSurfaceView.setOnClickListener(v -> onPlayRectClick());
    }

    @Override
    void changeToPortrait(ActivityDramaEditBinding binding) {
        mIsLandscape = false;
        binding.pickView.setVisibility(View.VISIBLE);
        binding.viewToolbarDarkInset.insetView.setVisibility(View.VISIBLE);
        binding.viewToolbarDarkInset.toolbar.setVisibility(View.VISIBLE);
        binding.viewFullBottom.getRoot().setVisibility(View.GONE);
        binding.viewFullTop.getRoot().setVisibility(View.GONE);
        binding.btnFull.setVisibility(View.VISIBLE);
        mActivity.setSupportActionBar(binding.viewToolbarDarkInset.toolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        mVideoPlayerManager.setStopTouchToRestart(false);
        mActivity.invalidateOptionsMenu();
    }

    @Override
    void changeToLandscape(ActivityDramaEditBinding binding) {
        binding.pickView.setVisibility(View.GONE);
        binding.viewToolbarDarkInset.insetView.setVisibility(View.GONE);
        binding.viewToolbarDarkInset.toolbar.setVisibility(View.GONE);
        binding.viewFullTop.getRoot().setVisibility(View.VISIBLE);
        binding.viewFullBottom.getRoot().setVisibility(View.VISIBLE);
        updateBottomLayoutPadding(mActivity, binding.viewFullBottom.layoutToolBottom);
        binding.btnFull.setVisibility(View.GONE);
        mActivity.setSupportActionBar(binding.viewFullTop.toolbarFull);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        mVideoPlayerManager.setStopTouchToRestart(true);
        mIsLandscape = true;
        mActivity.invalidateOptionsMenu();
    }

    public void update(MenuItem menuShareItem) {
        menuShareItem.setVisible(!mIsLandscape);
    }

    public void setVideoPlayerManager(VideoPlayerManager videoPlayerManager) {
        mVideoPlayerManager = videoPlayerManager;
        mVideoPlayerManager.addProgressChangeListener(this);
    }

    private void hideTool() {
        mHideToolSubject.onNext(new Object());
    }

    private void hideAllBar() {
        if (mVideoPlayerManager.getGLThread().isStop() || !mToolShow) return;
        mToolShow = false;
        ObjectAnimator.ofFloat(mBinding.viewFullBottom.layoutToolBottom, View.TRANSLATION_Y, 0,
                mBinding.viewFullBottom.layoutToolBottom.getHeight()).start();
        ObjectAnimator.ofFloat(mBinding.viewFullTop.layoutToolTop, View.TRANSLATION_Y, 0, -mBinding.viewFullTop.layoutToolTop.getHeight()).start();
    }

    private void showAllBar() {
        if (mToolShow) return;
        mToolShow = true;
        ObjectAnimator.ofFloat(mBinding.viewFullBottom.layoutToolBottom, View.TRANSLATION_Y,
                mBinding.viewFullBottom.layoutToolBottom.getHeight(), 0).start();
        ObjectAnimator.ofFloat(mBinding.viewFullTop.layoutToolTop, View.TRANSLATION_Y, -mBinding.viewFullTop.layoutToolTop.getHeight(), 0).start();
    }

    private void onPlayRectClick() {
        if (!mToolShow) {
            showAllBar();
            hideTool();
        } else {
            if (mVideoPlayerManager.isStop()) {
                mVideoPlayerManager.startVideo();
            } else {
                mVideoPlayerManager.pauseVideo();
            }
        }
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.viewFullBottom.textTimeStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1 - progress);
        mBinding.viewFullBottom.textTimeEnd.setText(hmsTotal);
    }

    @Override
    public void onProgressStop() {
        mBinding.viewFullBottom.btnPausePlayBtn.setSelected(true);
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.viewFullBottom.textTimeStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1 - progress);
        mBinding.viewFullBottom.textTimeEnd.setText(hmsTotal);
    }

    @Override
    public void onProgressStart() {
        hideTool();
        mBinding.viewFullBottom.btnPausePlayBtn.setSelected(false);
    }
}
