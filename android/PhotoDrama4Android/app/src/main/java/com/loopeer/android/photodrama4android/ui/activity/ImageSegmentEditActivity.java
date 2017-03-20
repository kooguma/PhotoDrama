package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityImageClipEditBinding;
import com.loopeer.android.photodrama4android.opengl.GLTouchListener;
import com.loopeer.android.photodrama4android.opengl.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.opengl.VideoPlayerManager;
import com.loopeer.android.photodrama4android.opengl.model.Drama;
import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.model.ScaleTranslateRatio;
import com.loopeer.android.photodrama4android.opengl.render.SegmentPreviewRender;
import com.loopeer.android.photodrama4android.ui.adapter.ImageSegmentAdapter;

public class ImageSegmentEditActivity extends AppCompatActivity implements ImageSegmentAdapter.OnSelectedListener, GLTouchListener.ScaleMoveListener {

    private Drama mDrama;
    private ImageSegmentAdapter mSegmentAdapter;
    private ActivityImageClipEditBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private GLTouchListener mGLTouchListener;
    private ImageClip mSelectedImageClip;

    private static final int POSITION_START = 0;
    private static final int POSITION_END = 1;
    private int mSelectedPosition = POSITION_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_clip_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        mVideoPlayerManager = new VideoPlayerManager(null, mBinding.glSurfaceView, mDrama);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

        mGLTouchListener = new GLTouchListener(mBinding.glSurfaceView);
        mGLTouchListener.setScaleMoveListener(this);
        mBinding.glSurfaceView.setOnTouchListener(mGLTouchListener);

        mBinding.glSegmentStart.setRenderer(new SegmentPreviewRender());
        mBinding.glSegmentLeft.setRenderer(new SegmentPreviewRender());
        updateImageSegmentList();

        mBinding.textEnd.setOnClickListener(v -> {
            selectedImageClipEnd();
        });

        mBinding.textStart.setOnClickListener(v -> {
            selectedImageClipStart();
        });

    }

    public void onPlayClick(View view) {
        mVideoPlayerManager.seekToVideo(mSelectedImageClip.startTime);
        mVideoPlayerManager.startVideo();
    }

    private void updateScaleListenerValue(ScaleTranslateRatio scaleTranslateRatio) {
        mGLTouchListener.updateFactorXY(scaleTranslateRatio.scaleFactor
                , scaleTranslateRatio.x
                , scaleTranslateRatio.y);
    }

    private void selectedImageClipStart() {
        mBinding.textStart.setSelected(true);
        mBinding.textEnd.setSelected(false);
        mSelectedPosition = POSITION_START;
        updateVideoToStartTime(mSelectedImageClip);
        updateScaleListenerValue(mSelectedImageClip.startScaleTransRatio);
    }

    private void selectedImageClipEnd() {
        mBinding.textStart.setSelected(false);
        mBinding.textEnd.setSelected(true);
        mSelectedPosition = POSITION_END;
        updateVideoToEndTime(mSelectedImageClip);
        updateScaleListenerValue(mSelectedImageClip.endScaleTransRatio);
    }

    private void updateImageSegmentList() {
        mSegmentAdapter = new ImageSegmentAdapter(this);
        mSegmentAdapter.setOnSelectedListener(this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerView.setAdapter(mSegmentAdapter);
        mSegmentAdapter.updateData(mDrama.videoGroup.imageClips);
    }

    @Override
    public void onImageSelected(ImageClip imageClip) {
        mSelectedImageClip = imageClip;
        mSelectedPosition = POSITION_START;
        selectedImageClipStart();
    }

    private void updateVideoToStartTime(ImageClip imageClip) {
        mVideoPlayerManager.updateVideoTime(imageClip.startTime, imageClip.getEndTime());
        mVideoPlayerManager.seekToVideo(imageClip.startTime);
    }

    private void updateVideoToEndTime(ImageClip imageClip) {
        mVideoPlayerManager.updateVideoTime(imageClip.startTime, imageClip.getEndTime());
        mVideoPlayerManager.seekToVideo(imageClip.getEndTime());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.menu_done) {
            Intent intent = new Intent();
            intent.putExtra(Navigator.EXTRA_DRAMA, mVideoPlayerManager.getDrama());
            setResult(RESULT_OK, intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void gLViewScale(float scaleFactor) {
        if (mSelectedPosition == POSITION_START) {
            mSelectedImageClip.startScaleTransRatio.scaleFactor = scaleFactor;
        } else {
            mSelectedImageClip.endScaleTransRatio.scaleFactor = scaleFactor;
        }
        mVideoPlayerManager.getGLThread().requestRender();
    }

    @Override
    public void gLViewMove(float x, float y) {
        if (mSelectedPosition == POSITION_START) {
            mSelectedImageClip.startScaleTransRatio.x = x;
            mSelectedImageClip.startScaleTransRatio.y = y;
        } else {
            mSelectedImageClip.endScaleTransRatio.x = x;
            mSelectedImageClip.endScaleTransRatio.y = y;
        }
        mVideoPlayerManager.getGLThread().requestRender();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
