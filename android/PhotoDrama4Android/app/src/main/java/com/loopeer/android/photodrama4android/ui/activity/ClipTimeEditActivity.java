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
import com.loopeer.android.photodrama4android.databinding.ActivityClipTimeEditBinding;
import com.loopeer.android.photodrama4android.databinding.ActivityTransitionEditBinding;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.media.model.TransitionType;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ImageTransitionSegmentAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.TransitionEffectAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClipTimeEditActivity extends PhotoDramaBaseActivity implements ImageTransitionSegmentAdapter.OnSelectedListener, TransitionEffectAdapter.OnSelectedListener {

    private ActivityClipTimeEditBinding mBinding;
    private ImageTransitionSegmentAdapter mImageTransitionSegmentAdapter;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private TransitionClip mSelectedTransitionClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_clip_time_edit);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, mDrama, null);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

        updateRecyclerView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateRecyclerView() {
        mImageTransitionSegmentAdapter = new ImageTransitionSegmentAdapter(this);
        mImageTransitionSegmentAdapter.setOnSelectedListener(this);
        mBinding.recyclerViewSegment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerViewSegment.setAdapter(mImageTransitionSegmentAdapter);
        mImageTransitionSegmentAdapter.updateData(ClipsCreator.createTransiImageWrappers(mDrama.videoGroup));

        mImageTransitionSegmentAdapter.selectedFirstTransition();
    }

    private List<TransitionClip> createTransitionClips() {
        List<TransitionClip> transitionClips = new ArrayList<>();
        TransitionType[] types = TransitionType.values();
        for (int i = 0; i < types.length; i++) {
            TransitionClip clip = new TransitionClip(types[i]);
            if (types[i] == TransitionType.NO)
                clip.showTime = 0;
            transitionClips.add(clip);
        }
        return transitionClips;
    }

    @Override
    public void onImageTransitionSelected(TransitionImageWrapper transitionImageWrapper) {
        if (transitionImageWrapper.isImageClip()) return;

    }

    @Override
    public void onEffectSelected(TransitionClip transitionClip) {
        mImageTransitionSegmentAdapter.notifyTransition(transitionClip);
        updateDramaImageAndTransitionTime();
        mSelectedTransitionClip = mImageTransitionSegmentAdapter.getSelectedTransition();
        mVideoPlayerManager.refreshTransitionRender();

        mVideoPlayerManager.updateVideoTime(mSelectedTransitionClip.startTime
                , mSelectedTransitionClip.getEndTime());
        mVideoPlayerManager.seekToVideo(mSelectedTransitionClip.startTime);
    }

    private void updateDramaImageAndTransitionTime() {
        ClipsCreator.updateImageTransitionClips(mDrama.videoGroup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void onPlayClick(View view) {
        mVideoPlayerManager.seekToVideo(mSelectedTransitionClip.startTime);
        mVideoPlayerManager.startVideoWithFinishTime(mSelectedTransitionClip.startTime);
    }

}
