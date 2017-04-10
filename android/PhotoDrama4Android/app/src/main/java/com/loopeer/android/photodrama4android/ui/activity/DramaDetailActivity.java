package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.SeriesService;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaDetailBinding;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.utils.DramaFetchHelper;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.hepler.ILoader;
import com.loopeer.android.photodrama4android.ui.hepler.ThemeLoader;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaDetailActivity extends PhotoDramaBaseActivity {
    private ActivityDramaDetailBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private DramaFetchHelper mDramaFetchHelper;
    private ILoader mLoader;
    private Theme mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_drama_detail);
        setupView();
        parseIntent();
        loadDrama(mTheme);
        updateSeries();
    }

    private void parseIntent() {
        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);
        if (mTheme != null) {
            mBinding.setTheme(mTheme);
        }
    }

    private void loadDrama(Theme theme) {
        if (theme == null) return;
        mLoader.showProgress();
        mDramaFetchHelper = new DramaFetchHelper(this);
        mDramaFetchHelper.getDrama(theme,
            drama -> {
                mVideoPlayerManager.updateDrama(drama);
                mVideoPlayerManager.seekToVideo(0);
                mVideoPlayerManager.startVideo();
            }, throwable -> {
                throwable.printStackTrace();
                mLoader.showMessage(throwable.getMessage());
            }, () -> {
                mLoader.showContent();
            });
    }

    private void updateSeries() {
        if (mTheme == null) return;
        registerSubscription(
            ResponseObservable.unwrap(SeriesService.INSTANCE.detail(mTheme.seriesId))
                .subscribe(series -> {
                    mBinding.setSeries(series);
                    for (int i = 0; i < series.themes.size(); i++) {
                        final Theme theme = series.themes.get(i);
                        mBinding.layoutEpisode.addView(generaEpisodeButton(i + 1, theme));
                    }
                    final int index = series.getSeriesIndex(mTheme) - 1;
                    final View child = mBinding.layoutEpisode.getChildAt(index);
                    setSelected(child);
                    mBinding.scrollViewEpisode.post(
                        () -> mBinding.scrollViewEpisode.scrollTo(child.getLeft(), 0));
                }, throwable -> {
                    throwable.printStackTrace();
                    showToast(throwable.toString());
                }, () -> dismissProgressLoading())
        );
    }

    private void setupView() {
        mLoader = new ThemeLoader(mBinding.animator);
        AppCompatSeekBar seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        findViewById(R.id.btn_pause_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (mVideoPlayerManager.isStop()) {
                    v.setBackgroundResource(R.drawable.ic_pause_white_large);
                    mVideoPlayerManager.startVideo();
                } else {
                    v.setBackgroundResource(R.drawable.ic_play_white_large);
                    mVideoPlayerManager.pauseVideo();
                }
            }
        });

        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(seekBar),
            mBinding.glSurfaceView, new Drama());
        mBinding.glSurfaceView.setOnClickListener(v -> mVideoPlayerManager.pauseVideo());
        mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.onRestart();
    }

    private Button generaEpisodeButton(int index, final Theme theme) {
        Button button = (Button) LayoutInflater.from(this)
            .inflate(R.layout.view_episode_button, mBinding.layoutEpisode, false);
        button.setText(getString(R.string.drama_index_format, index));
        button.setOnClickListener(v -> {
            if (!v.isSelected()) {
                mVideoPlayerManager.onStop();
                loadDrama(theme);
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

    public void onEditClick(View view) {
        Navigator.startDramaEditActivity(DramaDetailActivity.this, mTheme);
    }

    public void onCloseClick(View view) {
        onBackPressed();
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

}
