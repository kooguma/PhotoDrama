package com.loopeer.android.photodrama4android.ui.hepler;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaDetailBinding;

import static com.loopeer.android.photodrama4android.ui.hepler.FullBottomLayoutHelper.updateBottomLayoutPadding;

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
        updateBottomLayoutPadding(mContext, binding.layoutToolBottom);
        ((AppCompatActivity) mContext).getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

}
