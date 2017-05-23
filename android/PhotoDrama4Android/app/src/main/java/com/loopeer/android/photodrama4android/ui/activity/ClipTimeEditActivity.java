package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityClipTimeEditBinding;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ClipTimeEditAdapter;
import com.loopeer.android.photodrama4android.ui.widget.TimeSelectView;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class ClipTimeEditActivity extends PhotoDramaBaseActivity implements ClipTimeEditAdapter.OnSelectedListener, TimeSelectView.TimeUpdateListener, VideoPlayerManager.ProgressChangeListener {

    private ActivityClipTimeEditBinding mBinding;
    private ClipTimeEditAdapter mClipTimeEditAdapter;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_clip_time_edit);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, mDrama);
        mVideoPlayerManager.addProgressChangeListener(this);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.seekToVideo(0);
        mBinding.glSurfaceView.setOnClickListener(v -> onPlayRectClick());
        mBinding.timeProgressView.setTimeUpdateListener(this);
        updateRecyclerView();
        checkNavigationBarChangeMargin();
    }

    private void checkNavigationBarChangeMargin() {
        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {//have navi
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBinding.textSelectedTransition.getLayoutParams();
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.large_padding);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
    }

    private void updateRecyclerView() {
        mClipTimeEditAdapter = new ClipTimeEditAdapter(this);
        mClipTimeEditAdapter.setOnSelectedListener(this);
        mBinding.recyclerViewSegment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerViewSegment.setAdapter(mClipTimeEditAdapter);
        mClipTimeEditAdapter.updateData(mDrama.videoGroup.imageClips);

        mClipTimeEditAdapter.selectedFirstTransition();
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

    public void onPlayClick(View view) {
        mVideoPlayerManager.seekToVideo(mClipTimeEditAdapter.getSelectedImageClip().startTime);
        mVideoPlayerManager.startVideoWithFinishTime(mClipTimeEditAdapter.getSelectedImageClip().startTime);
    }

    @Override
    public void onImageClipSelected(ImageClip imageClip) {
        mBinding.timeProgressView.updateProgress(imageClip.showTime);
        mVideoPlayerManager.updateVideoTime(imageClip.startTime
                , imageClip.getEndTime());
        mVideoPlayerManager.seekToVideo(imageClip.startTime);
        updateBtnEnable();
    }

    @Override
    public void onTimeUpdate(int time) {
        updateBtnEnable();
    }

    public void onBtnClick(View view) {
        mClipTimeEditAdapter.getSelectedImageClip().showTime = mBinding.timeProgressView.getProgress();
        mClipTimeEditAdapter.notifyDataSetChanged();
        ClipsCreator.updateImageClipsByShowTime(mDrama.videoGroup);
        mVideoPlayerManager.updateVideoTime(mClipTimeEditAdapter.getSelectedImageClip().startTime
                , mClipTimeEditAdapter.getSelectedImageClip().getEndTime());
        updateBtnEnable();
    }

    private void updateBtnEnable() {
        mBinding.btnConfirm.setEnabled(mClipTimeEditAdapter.getSelectedImageClip().showTime != mBinding.timeProgressView.getProgress());
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {

    }

    @Override
    public void onProgressStop() {
        mBinding.btnPlay.setVisibility(View.VISIBLE);

    }

    @Override
    public void onProgressChange(int progress, int maxValue) {

    }

    @Override
    public void onProgressStart() {
        mBinding.btnPlay.setVisibility(View.GONE);
    }

    private void onPlayRectClick() {
        if (mVideoPlayerManager.isStop()) {
            mVideoPlayerManager.startVideo();
        } else {
            mVideoPlayerManager.pauseVideo();
        }
    }

    @Override
    public void finish() {
        doBeforeFinish();
        super.finish();
    }

    private void doBeforeFinish() {
        Intent intent = new Intent();
        intent.putExtra(Navigator.EXTRA_DRAMA, mVideoPlayerManager.getDrama());
        setResult(RESULT_OK, intent);
    }
}
