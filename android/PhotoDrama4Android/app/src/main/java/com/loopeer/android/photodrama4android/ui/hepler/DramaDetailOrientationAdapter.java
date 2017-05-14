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
        binding.layoutToolBottom.setPadding(0, 0, 0, 0);
        ((AppCompatActivity) mContext).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    void changeToLandscape(ActivityDramaDetailBinding binding) {
        binding.btnFull.setVisibility(View.GONE);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.large_padding);
        binding.layoutToolBottom.setPadding(
                padding, padding, padding, padding);
        ((AppCompatActivity) mContext).getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

}
