package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityMakeMovieBinding;
import com.loopeer.android.photodrama4android.opengl.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.opengl.VideoPlayerManager;
import com.loopeer.android.photodrama4android.opengl.model.Drama;

import java.text.SimpleDateFormat;

public class MakeMovieActivity extends MovieMakerBaseActivity implements VideoPlayerManager.ProgressChangeListener {

    private ActivityMakeMovieBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private Drama mDrama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_make_movie);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVideoPlayerManager = new VideoPlayerManager(mBinding.seekBar, mBinding.glSurfaceView, mDrama);
        mVideoPlayerManager.setProgressChangeListener(this);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

        mBinding.glSurfaceView.setOnClickListener(v -> mVideoPlayerManager.pauseVideo());

        mVideoPlayerManager.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
    public void onProgressInit(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
        String hms = formatter.format(progress);
        mBinding.textStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1);
        mBinding.textTotal.setText(hmsTotal);
    }

    @Override
    public void onProgressStop() {
        mBinding.btnPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChange(int progress) {
        SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
        String hms = formatter.format(progress);
        mBinding.textStart.setText(hms);
    }

    @Override
    public void onProgressStart(int progress, int maxValue) {
        if (mBinding.btnPlay.getVisibility() == View.VISIBLE) mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onPlayBtnClick(View view) {
        mVideoPlayerManager.startVideo();
        mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onStartFragmentEdit(View view) {
        Navigator.startImageClipEditActivity(this, mDrama);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Navigator.REQUEST_CODE_DRAMA_IMAGE_EDIT:
                    Drama drama = (Drama) data.getSerializableExtra(Navigator.EXTRA_DRAMA);
                    if (drama != null)
                        mDrama = drama;
                    mVideoPlayerManager.updateDrama(mDrama);
                    break;
                default:
            }
            mVideoPlayerManager.seekToVideo(0);
        }
    }
}
