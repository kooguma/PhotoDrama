package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityGuideBinding;
import java.util.ArrayList;
import java.util.List;


public class GuideActivity extends PhotoDramaBaseActivity implements ViewPager.OnPageChangeListener {
    private static final int[] pics = {R.drawable.ic_guide_1, R.drawable.ic_guide_2, R.drawable.ic_guide_3};

    private List<View> mGuideViewList = new ArrayList<>();
    private int mPreIndicatPosition = 0;
    private ActivityGuideBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_guide);
        fullScreen();
        setSwipeBackEnable(false);

        initIndicator();
        initGuideView();
        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreen();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        fullScreen();
    }

    private void fullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initIndicator() {
       /* mPageIndicator = new PageIndicator(GuideActivity.this);
        mPageIndicator.setIndicatorMargin(
                getResources().getDimensionPixelSize(R.dimen.xsmall_padding));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layoutParams.bottomMargin = DeviceScreenUtils.dp2px(50, this);
        mBinding.container.addView(mPageIndicator, layoutParams);
        mPageIndicator.updateCount(pics.length);*/
        mBinding.viewIndicator.updateCount(pics.length);
        mBinding.viewIndicator.setIndicatorMargin(
                getResources().getDimensionPixelSize(R.dimen.xsmall_padding));
    }

    private void initGuideView() {
        for (int i = 0; i < pics.length; i++) {
            View v = View.inflate(this, R.layout.view_guide, null);
            ((ImageView) v.findViewById(R.id.img)).setImageResource(pics[i]);
            mGuideViewList.add(v);
        }
    }

    private void initViewPager() {
        GuidePagerAdapter guidePagerAdapter = new GuidePagerAdapter(mGuideViewList);
        mBinding.pager.setAdapter(guidePagerAdapter);
        mBinding.pager.addOnPageChangeListener(this);
    }

    public void onGoClick(View v) {
        Navigator.startMainActivity(this);
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mBinding.viewIndicator.updatePosition(position);
        if (mPreIndicatPosition != 2 && position == 2) {
            mBinding.layoutAnimator.setDisplayedChild(1);
        } else if (mPreIndicatPosition == 2 && position != 2){
            mBinding.layoutAnimator.setDisplayedChild(0);
        }
        mPreIndicatPosition = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    static class GuidePagerAdapter extends PagerAdapter {

        private List<View> mViews;

        private GuidePagerAdapter(List<View> list) {
            mViews = list;
        }

        @Override
        public int getCount() {
            return pics.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViews.get(position));
            return mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }
    }
}
