package com.loopeer.android.photodrama4android.ui.hepler;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaDetailBinding;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaEditBinding;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;

import static com.loopeer.android.photodrama4android.ui.hepler.FullBottomLayoutHelper.updateBottomLayoutPadding;

public class DramaEditOrientationAdapter extends OrientationAdapter<ActivityDramaEditBinding> {

    private VideoPlayerManager mVideoPlayerManager;
    private boolean mIsLandscape;

    public DramaEditOrientationAdapter(ActivityDramaEditBinding activityDataBinding) {
        super(activityDataBinding);
    }

    @Override
    void changeToPortrait(ActivityDramaEditBinding binding) {
        mIsLandscape = false;
        binding.pickView.setVisibility(View.VISIBLE);
        binding.viewToolbarDarkInset.insetView.setVisibility(View.VISIBLE);
        binding.viewToolbarDarkInset.toolbar.setVisibility(View.VISIBLE);
        binding.viewFullBottom.getRoot().setVisibility(View.GONE);
        binding.viewFullTop.getRoot().setVisibility(View.GONE);
        binding.btnFull.setVisibility(View.VISIBLE);
        ((AppCompatActivity) mContext).setSupportActionBar(binding.viewToolbarDarkInset.toolbar);
        ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        mVideoPlayerManager.setStopTouchToRestart(false);
        ((AppCompatActivity) mContext).invalidateOptionsMenu();
    }

    @Override
    void changeToLandscape(ActivityDramaEditBinding binding) {
        binding.pickView.setVisibility(View.GONE);
        binding.viewToolbarDarkInset.insetView.setVisibility(View.GONE);
        binding.viewToolbarDarkInset.toolbar.setVisibility(View.GONE);
        binding.viewFullTop.getRoot().setVisibility(View.VISIBLE);
        binding.viewFullBottom.getRoot().setVisibility(View.VISIBLE);
        updateBottomLayoutPadding(mContext, binding.viewFullBottom.layoutToolBottom);
        binding.btnFull.setVisibility(View.GONE);
        ((AppCompatActivity) mContext).setSupportActionBar(binding.viewFullTop.toolbarFull);
        ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) mContext).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        mVideoPlayerManager.setStopTouchToRestart(true);
        mIsLandscape = true;
        ((AppCompatActivity) mContext).invalidateOptionsMenu();
    }

    public void update(MenuItem menuShareItem) {
        menuShareItem.setVisible(!mIsLandscape);
    }

    public void setVideoPlayerManager(VideoPlayerManager videoPlayerManager) {
        mVideoPlayerManager = videoPlayerManager;
    }
}
