package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityImageClipEditBinding;
import com.loopeer.android.photodrama4android.opengl.VideoPlayerManager;
import com.loopeer.android.photodrama4android.opengl.model.Drama;
import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.render.SegmentPreviewRender;
import com.loopeer.android.photodrama4android.ui.adapter.ImageSegmentAdapter;

public class ImageSegmentEditActivity extends AppCompatActivity implements ImageSegmentAdapter.OnSelectedListener {

    private static final String TAG = "ImageSegmentEditActivit";
    private Drama mDrama;
    private ImageSegmentAdapter mSegmentAdapter;
    private ActivityImageClipEditBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private float mScaleFactor = 1.f;

    private ScaleGestureDetector mScaleDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_clip_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        mVideoPlayerManager = new VideoPlayerManager(null, mBinding.glSurfaceView, mDrama);

        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        mBinding.glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                Log.e(TAG, "ontouch");
                return true;
            }
        });

        mBinding.glSegmentStart.setRenderer(new SegmentPreviewRender());
        mBinding.glSegmentLeft.setRenderer(new SegmentPreviewRender());
        updateImageSegmentList();
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            Log.e(TAG, mScaleFactor + " ");
            return true;
        }
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
        mVideoPlayerManager.updateVideoTime(imageClip.startTime, imageClip.getEndTime());
        mVideoPlayerManager.seekToVideo(imageClip.startTime);
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
        mVideoPlayerManager.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
