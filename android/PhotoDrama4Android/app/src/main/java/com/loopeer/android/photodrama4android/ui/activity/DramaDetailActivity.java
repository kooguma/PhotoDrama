package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaDetailActivity extends PhotoDramaBaseActivity {
    private ActivityDramaDetailBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;
    private LinearLayout mLayoutEpisode;
    private AppCompatSeekBar mSeekBar;
    private DramaFetchHelper mDramaFetchHelper;
    private Theme mTheme;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_drama_detail);
        mMovieMakerGLSurfaceView = (MovieMakerGLSurfaceView) findViewById(R.id.gl_surface_view);
        setupView();
        parseIntent();
        loadDrama();
        updateSeries();
    }

    private void parseIntent() {
        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);
        if (mTheme != null) {
            mBinding.setTheme(mTheme);
        }
    }

    private void loadDrama() {
        if (mTheme == null) return;
        showProgressLoading("");
        mDramaFetchHelper = new DramaFetchHelper(this);
        mDramaFetchHelper.getDrama(mTheme,
            drama -> {
                mVideoPlayerManager.updateDrama(drama);
                showToast(R.string.drama_unzip_success);
            }, throwable -> {
                throwable.printStackTrace();
                showToast(throwable.toString());
            }, () -> {
                dismissProgressLoading();
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
                        mLayoutEpisode.addView(generaEpisodeButton(i+1,theme));
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    showToast(throwable.toString());
                }, () -> dismissProgressLoading())
        );
    }

    private void setupView() {
        mSeekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mSeekBar),
            mMovieMakerGLSurfaceView, new Drama());
        mLayoutEpisode = (LinearLayout) findViewById(R.id.layout_episode);
        mMovieMakerGLSurfaceView.setOnClickListener(v -> mVideoPlayerManager.pauseVideo());
        mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.onRestart();
        mButton = (Button) findViewById(R.id.btn_use_drama);
        mButton.setOnClickListener(v -> {
            Navigator.startDramaEditActivity(DramaDetailActivity.this, mTheme);
        });
    }

    private Button generaEpisodeButton(int index,final Theme theme) {
        Button button = (Button) LayoutInflater.from(this)
            .inflate(R.layout.view_episode_button, mLayoutEpisode, false);
        button.setText(getString(R.string.drama_index_format,index));
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setSelected(v);
            }
        });
        return button;
    }

    private void setSelected(View btn){
        final int count = mLayoutEpisode.getChildCount();
        for (int i = 0 ; i < count ; i++){
            View v = mLayoutEpisode.getChildAt(i);
            if(v.equals(btn)){
                v.setSelected(true);
            }else {
                v.setSelected(false);
            }
        }

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
