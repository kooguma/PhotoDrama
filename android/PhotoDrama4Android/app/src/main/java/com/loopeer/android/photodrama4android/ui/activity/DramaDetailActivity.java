package com.loopeer.android.photodrama4android.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.SeriesService;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaDetailBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.utils.DramaFetchHelper;
import com.loopeer.android.photodrama4android.model.Series;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.hepler.DramaDetailOrientationAdapter;
import com.loopeer.android.photodrama4android.ui.hepler.ILoader;
import com.loopeer.android.photodrama4android.ui.hepler.OrientationAdapter;
import com.loopeer.android.photodrama4android.ui.hepler.ScreenOrientationHelper;
import com.loopeer.android.photodrama4android.ui.hepler.ThemeLoader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaDetailActivity extends PhotoDramaBaseActivity
    implements VideoPlayerManager.ProgressChangeListener {
    private ActivityDramaDetailBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private DramaFetchHelper mDramaFetchHelper;
    private ILoader mLoader;
    private Drama mDrama;
    //默认第一集
    private Theme mTheme;
    private Series mSeries;
    private String mSeriesId;
    private Subject<Theme> mLoadSubject = PublishSubject.create();
    private Subject mHideToolSubject = PublishSubject.create();
    private boolean mToolShow = true;
    private int mUsedTime;
    private ScreenOrientationHelper mScreenOrientationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_drama_detail);
        setupView();
        parseIntent();
        updateSeries(mSeries == null ? mSeriesId : mSeries.id, true);
        registerSubscription(
            mLoadSubject.debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> loadDrama(o))
                .subscribe()
        );
        registerSubscription(
            mHideToolSubject.debounce(getResources().getInteger(R.integer.movie_show_time),
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> hideAllBar())
                .subscribe()
        );

        getChangingConfigurations();

        OrientationAdapter orientationAdapter = new DramaDetailOrientationAdapter(mBinding, this);
        mScreenOrientationHelper = new ScreenOrientationHelper(this
            , getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            , orientationAdapter);
    }

    private void parseIntent() {
        mSeries = (Series) getIntent().getSerializableExtra(Navigator.EXTRA_SERIES);
        mSeriesId = getIntent().getStringExtra(Navigator.EXTRA_SERIES_ID);
    }

    private void loadDramaSend(Theme theme) {
        showToolbar();
        mLoader.showProgress();
        mLoadSubject.onNext(theme);
    }

    private void loadDrama(Theme theme) {
        if (theme == null) return;
        Analyst.dramaDetailClick(theme.id);
        if (mDramaFetchHelper == null) mDramaFetchHelper = new DramaFetchHelper(this);
        mDramaFetchHelper.checkSubscribe();
        mDramaFetchHelper.getDrama(theme,
            drama -> {
                updateTitle(theme);
                mVideoPlayerManager.updateDrama(drama);
                mVideoPlayerManager.seekToVideo(mUsedTime);
                mVideoPlayerManager.startVideo();
                mDrama = drama;
            }, throwable -> {
                throwable.printStackTrace();
                mLoader.showMessage(getString(R.string.drama_load_fail));
            }, () -> mLoader.showContent());
    }

    private void updateTitle(Theme theme) {
        String title;
        if (mSeries.themesCount == 1) {
            title = mSeries.name;
        } else {
            title = mSeries.name
                + " - "
                + String.format("%02d", Integer.valueOf(theme.episodeNumber));
        }
        getSupportActionBar().setTitle(title);
    }

    private void updateSeries(String sersId, boolean isFirstLoad) {
        if (TextUtils.isEmpty(sersId)) return;
        registerSubscription(
            ResponseObservable.unwrap(SeriesService.INSTANCE.detail(sersId))
                .subscribe(series -> {
                    mBinding.setSeries(series);
                    mBinding.setTheme(mTheme = series.firstTheme());
                    loadDramaSend(mTheme);
                    if (isFirstLoad && series.themesCount > 1) {
                        mBinding.containerEpisode.setVisibility(View.VISIBLE);
                        for (int i = 0; i < series.themes.size(); i++) {
                            mBinding.layoutEpisode.addView(
                                generaEpisodeButton(series.themes.get(i)));
                        }
                        updateSelectedThemeBtn();
                    } else {
                        mBinding.containerEpisode.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    showToast(throwable.toString());
                }, () -> dismissProgressLoading())
        );
    }

    private void updateSelectedThemeBtn() {
        int index = Integer.parseInt(mTheme.episodeNumber) - 1;
        View child = mBinding.layoutEpisode.getChildAt(index);
        setSelected(child);
        mBinding.scrollViewEpisode.post(
            () -> {
                if (child != null) {
                    mBinding.scrollViewEpisode.scrollTo(child.getLeft(), 0);
                }
            });
    }

    private void setupView() {
        mLoader = new ThemeLoader(mBinding.animator);
        AppCompatSeekBar seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, new Drama(),
            new SeekWrapper(seekBar));
        mBinding.glSurfaceView.setOnClickListener(v -> onPlayRectClick());
        mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.addProgressChangeListener(this);
    }

    private Button generaEpisodeButton(final Theme theme) {
        Button button = (Button) LayoutInflater.from(this)
            .inflate(R.layout.view_episode_button, mBinding.layoutEpisode, false);

        button.setText(
            getString(R.string.drama_index_format, Integer.valueOf(theme.episodeNumber)));

        button.setOnClickListener(v -> {
            if (!v.isSelected()) {
                mUsedTime = 0;
                mVideoPlayerManager.stopVideo();
                loadDramaSend(theme);
                mTheme = theme;
                updateSelectedThemeBtn();
            }
            setSelected(v);
        });
        return button;
    }

    private void setSelected(View btn) {
        final int count = mBinding.layoutEpisode.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = mBinding.layoutEpisode.getChildAt(i);
            if (v.equals(btn)) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }

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

    private void hideTool() {
        mHideToolSubject.onNext(new Object());
    }

    private void hideAllBar() {
        if (mVideoPlayerManager.getGLThread().isStop() || !mToolShow) return;
        mToolShow = false;
        ObjectAnimator.ofFloat(mBinding.layoutToolBottom, View.TRANSLATION_Y, 0,
            mBinding.layoutToolBottom.getHeight()).start();
        ObjectAnimator.ofFloat(mBinding.layoutToolTop, View.TRANSLATION_Y, 0,
            -mBinding.layoutToolTop.getHeight()).start();
    }

    private void showToolbar() {
        if (mBinding.layoutToolTop.getTranslationY() < 0) {
            ObjectAnimator.ofFloat(mBinding.layoutToolTop, View.TRANSLATION_Y,
                -mBinding.layoutToolTop.getHeight(), 0).start();
        }
    }

    private void showAllBar() {
        if (mToolShow) return;
        mToolShow = true;
        ObjectAnimator.ofFloat(mBinding.layoutToolBottom, View.TRANSLATION_Y,
            mBinding.layoutToolBottom.getHeight(), 0).start();
        ObjectAnimator.ofFloat(mBinding.layoutToolTop, View.TRANSLATION_Y,
            -mBinding.layoutToolTop.getHeight(), 0).start();
    }

    public void onEditClick(View view) {
        Analyst.myStarDetailUseStoryClick(mTheme.id);
        Navigator.startDramaEditActivity(DramaDetailActivity.this, mTheme);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoPlayerManager.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoPlayerManager.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayerManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayManagerContainer.getDefault().onFinish(this);
        if (mVideoPlayerManager != null) {
            mVideoPlayerManager.onDestroy();
        }
        if (mDramaFetchHelper != null) {
            mDramaFetchHelper.unSubscribe();
        }
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.textTimeStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1 - progress);
        mBinding.textTimeEnd.setText(hmsTotal);
    }

    @Override
    public void onProgressStop() {
        mBinding.btnPausePlayBtn.setSelected(true);
        mBinding.btnPlayCenter.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mBinding.textTimeStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1 - progress);
        mBinding.textTimeEnd.setText(hmsTotal);
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

    public void onFullBtnClick(View view) {
        mScreenOrientationHelper.fullScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Navigator.REQUEST_FULL_SCREEN) {
            boolean restart = data.getBooleanExtra(Navigator.EXTRA_IS_TO_START, false);
            int usedTime = data.getIntExtra(Navigator.EXTRA_USEDTIME, -1);
            if (usedTime != -1) {
                mUsedTime = usedTime;
                if (mVideoPlayerManager != null) {
                    mVideoPlayerManager.seekToVideo(usedTime);
                    if (restart) {
                        mVideoPlayerManager.startVideo();
                    }
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mScreenOrientationHelper.backPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mScreenOrientationHelper.updateOrientation(
            newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

}
