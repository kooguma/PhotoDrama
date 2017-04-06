package com.loopeer.android.photodrama4android.ui.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;
import com.loopeer.android.photodrama4android.utils.FileManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaDetailActivity extends MovieMakerBaseActivity {
    private VideoPlayerManager mVideoPlayerManager;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;
    private AppCompatSeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_detail);
        mMovieMakerGLSurfaceView = (MovieMakerGLSurfaceView) findViewById(R.id.gl_surface_view);
        setupView();

        loadDrama();
    }

    private void loadDrama() {
        showProgressLoading("");
        Observable.create((Observable.OnSubscribe<Drama>) subscriber -> {
            subscriber.onStart();
            Drama drama = ZipUtils.unzipFile(FileManager.getInstance().getDir() + "/demo" + ".zip");
            subscriber.onNext(drama);
            subscriber.onCompleted();})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> mVideoPlayerManager.updateDrama(o))
                .doOnCompleted(() -> {
                    dismissProgressLoading();
                    showToast(R.string.drama_unzip_success);
                })
                .subscribe();
    }

    private void setupView() {
        mSeekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mSeekBar),
                mMovieMakerGLSurfaceView, new Drama());
        mMovieMakerGLSurfaceView.setOnClickListener(v -> mVideoPlayerManager.pauseVideo());
        mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.onRestart();

    }

}
