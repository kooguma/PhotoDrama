package com.loopeer.android.photodrama4android.ui.activity;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaPlayBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.utils.DramaFetchHelper;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.widget.ElasticDragDismissFrameLayout;

import com.loopeer.android.photodrama4android.ui.hepler.ILoader;
import com.loopeer.android.photodrama4android.ui.hepler.ThemeLoader;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaPlayActivity extends PhotoDramaBaseActivity implements VideoPlayerManager.ProgressChangeListener {

    private ActivityDramaPlayBinding mBinding;
    private Theme mTheme;
    private VideoPlayerManager mVideoPlayerManager;
    private Drama mDrama;
    private DramaFetchHelper mDramaFetchHelper;
    private ILoader mLoader;
    private Subject mHideToolSubject = PublishSubject.create();
    private boolean mToolShow = true;
    private boolean mBottomDismiss = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_drama_play);

        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);
        setupView();
        loadDrama();

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

    private void hideAllBar() {
        if (mVideoPlayerManager.getGLThread().isStop() || !mToolShow) return;
        mToolShow = false;
        ObjectAnimator.ofFloat(mBinding.layoutToolBottom, View.TRANSLATION_Y, 0, mBinding.layoutToolBottom.getHeight()).start();
    }

    private void showAllBar() {
        if (mToolShow) return;
        mToolShow = true;
        ObjectAnimator.ofFloat(mBinding.layoutToolBottom, View.TRANSLATION_Y, mBinding.layoutToolBottom.getHeight(), 0).start();
    }

    private void loadDrama() {
        if (mTheme == null) return;
        mLoader.showProgress();
        mDramaFetchHelper = new DramaFetchHelper(this);
        mDramaFetchHelper.getDrama(mTheme,
                drama -> {
                    updateDrama(drama);
                }, throwable -> {
                    throwable.printStackTrace();
                    mLoader.showMessage(throwable.getMessage());
                }, () -> {
                    mLoader.showContent();
                });
    }

    private void updateDrama(Drama drama) {
        mDrama = drama;
        mVideoPlayerManager.updateDrama(mDrama);
    }

    private void setupView() {
        mLoader = new ThemeLoader(mBinding.animator);
        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mBinding.seekBar),
                mBinding.glSurfaceView, new Drama());
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.setProgressChangeListener(this);
        mVideoPlayerManager.seekToVideo(0);
        mVideoPlayerManager.startVideo();

        mBinding.glSurfaceView.setOnClickListener(v -> {
            if (!mToolShow) {
                showAllBar();
                hideTool();
            } else {
                if (mVideoPlayerManager.isStop())
                    mVideoPlayerManager.startVideo();
                else
                    mVideoPlayerManager.pauseVideo();
            }
        });

        mBinding.dragContainer.addListener(new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
            @Override
            public void onDragDismissed() {
                finish();
            }

            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                if (rawOffset == 1.0f) {
                    mBottomDismiss = elasticOffsetPixels > 0.0f;
                }
            }
        });
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
        mDramaFetchHelper.unSubscribe();
        BitmapFactory.getInstance().clear();
    }

    public void onFullBtnClick(View view) {
        Navigator.startFullLandscapePlayActivity(this, mDrama);
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.textTimeStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1 - progress);
        mBinding.textTimeEnd.setText("-" + hmsTotal);
    }

    @Override
    public void onProgressStop() {
        mBinding.btnPausePlayBtn.setSelected(true);
        showAllBar();
    }

    @Override
    public void onProgressChange(int progress) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.textTimeStart.setText(hms);
        String hmsTotal = formatter.format(mVideoPlayerManager.getSeekbarMaxValue() + 1 - progress);
        mBinding.textTimeEnd.setText("- " + hmsTotal);
    }

    @Override
    public void onProgressStart() {
        hideTool();
        mBinding.btnPausePlayBtn.setSelected(false);
    }

    public void onCloseClick(View view) {
        finish();
    }

    public void onPausePlayBtnClick(View view) {
        if (mVideoPlayerManager.isStop()) {
            mVideoPlayerManager.startVideo();
        } else {
            mVideoPlayerManager.pauseVideo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Navigator.EXTRA_USEDTIME, mVideoPlayerManager.getUsedTime());
        outState.putBoolean(Navigator.EXTRA_IS_TO_START, !mVideoPlayerManager.isStop());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean restart = savedInstanceState.getBoolean(Navigator.EXTRA_IS_TO_START);
        int usedTime = savedInstanceState.getInt(Navigator.EXTRA_USEDTIME);
        if (mVideoPlayerManager != null) {
            mVideoPlayerManager.seekToVideo(usedTime);
            if (restart) {
                mVideoPlayerManager.startVideo();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mBottomDismiss) {
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom);
        } else {
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_top);
        }
    }
}
