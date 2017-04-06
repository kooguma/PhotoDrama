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
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DramaDetailActivity extends MovieMakerBaseActivity {
    private VideoPlayerManager mVideoPlayerManager;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;
    private AppCompatSeekBar mSeekBar;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_detail);
        mMovieMakerGLSurfaceView = (MovieMakerGLSurfaceView) findViewById(R.id.gl_surface_view);
        setupView();
    }

    private void setupView() {
        mSeekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        /*mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mSeekBar),
            mMovieMakerGLSurfaceView, new Drama());*/
        mMovieMakerGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            }

            @Override public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override public void onDrawFrame(GL10 gl) {

            }
        });
        mMovieMakerGLSurfaceView.setOnClickListener(v -> mVideoPlayerManager.pauseVideo());
       // mVideoPlayerManager.setCanRecord(true);
       // mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
       /// mVideoPlayerManager.onRestart();

    }

}
