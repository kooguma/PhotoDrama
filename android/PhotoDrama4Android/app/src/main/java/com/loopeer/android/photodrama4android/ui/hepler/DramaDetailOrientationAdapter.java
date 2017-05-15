package com.loopeer.android.photodrama4android.ui.hepler;

import android.view.View;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaDetailBinding;
import com.loopeer.android.photodrama4android.ui.activity.DramaDetailActivity;
import com.loopeer.android.photodrama4android.ui.activity.PhotoDramaBaseActivity;

import static com.loopeer.android.photodrama4android.ui.hepler.FullBottomLayoutHelper.updateBottomLayoutPadding;

public class DramaDetailOrientationAdapter extends OrientationAdapter<ActivityDramaDetailBinding, PhotoDramaBaseActivity> {

    public DramaDetailOrientationAdapter(ActivityDramaDetailBinding activityDataBinding, DramaDetailActivity activity) {
        super(activityDataBinding, activity);
    }

    @Override
    void changeToPortrait(ActivityDramaDetailBinding binding) {
        binding.btnFull.setVisibility(View.VISIBLE);
        binding.btnPlayCenterWrapper.setVisibility(View.GONE);
        binding.layoutToolBottom.setPadding(0, 0, 0, 0);
        binding.animator.setLandscape(false);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    void changeToLandscape(ActivityDramaDetailBinding binding) {
        binding.animator.setLandscape(true);
        binding.btnFull.setVisibility(View.GONE);
        binding.btnPlayCenterWrapper.setVisibility(View.VISIBLE);
        updateBottomLayoutPadding(mActivity, binding.layoutToolBottom);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

}
