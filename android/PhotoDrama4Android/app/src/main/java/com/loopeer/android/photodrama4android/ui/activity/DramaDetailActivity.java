package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.Button;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.utils.DramaFetchHelper;
import com.loopeer.android.photodrama4android.model.Theme;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaDetailActivity extends MovieMakerBaseActivity {
    private VideoPlayerManager mVideoPlayerManager;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;
    private AppCompatSeekBar mSeekBar;
    private DramaFetchHelper mDramaFetchHelper;
    private Theme mTheme;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_detail);
        mMovieMakerGLSurfaceView = (MovieMakerGLSurfaceView) findViewById(R.id.gl_surface_view);
        setupView();

        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);
        loadDrama();
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

    private void setupView() {
        mSeekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mSeekBar),
                mMovieMakerGLSurfaceView, new Drama());
        mMovieMakerGLSurfaceView.setOnClickListener(v -> mVideoPlayerManager.pauseVideo());
        mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.onRestart();
        mButton = (Button) findViewById(R.id.btn_use_drama);
        mButton.setOnClickListener(v -> {
            Navigator.startDramaEditActivity(DramaDetailActivity.this);
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

}
