package com.loopeer.android.photodrama4android.ui.hepler;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaDetailBinding;

public class DramaDetailOrientationAdapter extends OrientationAdapter<ActivityDramaDetailBinding> {

    public DramaDetailOrientationAdapter(ActivityDramaDetailBinding activityDataBinding) {
        super(activityDataBinding);
    }

    @Override
    void changeToPortrait(ActivityDramaDetailBinding binding) {
        binding.btnFull.setVisibility(View.VISIBLE);
        binding.btnPlayCenterWrapper.setVisibility(View.GONE);
        binding.layoutToolBottom.setPadding(0, 0, 0, 0);
        binding.animator.setLandscape(false);
        ((AppCompatActivity) mContext).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    void changeToLandscape(ActivityDramaDetailBinding binding) {
        binding.animator.setLandscape(true);
        binding.btnFull.setVisibility(View.GONE);
        binding.btnPlayCenterWrapper.setVisibility(View.VISIBLE);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.xsmall_padding);
        int paddingRight = mContext.getResources().getDimensionPixelSize(R.dimen.xxlarge_padding);
        int paddingLeft = mContext.getResources().getDimensionPixelSize(R.dimen.large_padding);
        binding.layoutToolBottom.setPadding(
                paddingLeft, padding, paddingRight, padding);
        ((AppCompatActivity) mContext).getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

}
