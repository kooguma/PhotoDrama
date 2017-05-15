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
    void changeToPortrait() {
        mBinding.btnFull.setVisibility(View.VISIBLE);
        mBinding.btnPlayCenterWrapper.setVisibility(View.GONE);
        mBinding.layoutToolBottom.setPadding(0, 0, 0, 0);
        mBinding.animator.setLandscape(false);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    void changeToLandscape() {
        mBinding.animator.setLandscape(true);
        mBinding.btnFull.setVisibility(View.GONE);
        mBinding.btnPlayCenterWrapper.setVisibility(View.VISIBLE);
        updateBottomLayoutPadding(mActivity, mBinding.layoutToolBottom);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

}
