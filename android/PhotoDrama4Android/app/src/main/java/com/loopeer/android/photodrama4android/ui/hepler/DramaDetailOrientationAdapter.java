package com.loopeer.android.photodrama4android.ui.hepler;

import android.view.View;

import com.loopeer.android.photodrama4android.R;
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
        mActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_transparent);
        mBinding.animator.setLandscape(false);
        mBinding.icBackBtn.setVisibility(View.VISIBLE);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    void changeToLandscape() {
        mBinding.animator.setLandscape(true);
        mBinding.btnFull.setVisibility(View.GONE);
        mBinding.btnPlayCenterWrapper.setVisibility(View.VISIBLE);
        mActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        mBinding.icBackBtn.setVisibility(View.GONE);
        updateBottomLayoutPadding(mActivity, mBinding.layoutToolBottom);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

}
