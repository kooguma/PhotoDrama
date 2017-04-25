package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CustomTabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.CategoryService;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.ui.fragment.DramaSelectFragment;
import com.loopeer.android.photodrama4android.utils.Toaster;
import java.util.ArrayList;
import java.util.List;

public class DramaSelectActivity extends PhotoDramaBaseActivity {

    private CustomTabLayout mCustomTabLayout;
    private ViewPager mViewPager;
    private DramaSelectFragment[] mFragments;
    private List<Category> mTitles = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_select);
        setupView();
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.drama_select);
    }

    private void setupView() {
        mCustomTabLayout = (CustomTabLayout) findViewById(R.id.drama_select_tab_select);
        mViewPager = (ViewPager) findViewById(R.id.drama_select_view_pager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Analyst.dramaCategoryClick(mTitles.get(position).id);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        registerSubscription(
            ResponseObservable.unwrap(this, CategoryService.INSTANCE.categories())
                .doOnTerminate(() -> {
                    mViewPager.setAdapter(
                        new DramaSelectViewPager(getSupportFragmentManager()));
                    mCustomTabLayout.setupWithViewPager(mViewPager);
                })
                .subscribe(categories -> {
                    if (categories != null && !categories.isEmpty()) {
                        mTitles.clear();
                        mTitles.addAll(categories);
                        mFragments = new DramaSelectFragment[categories.size()];
                        for (int i = 0; i < mTitles.size(); i++) {
                            final String title = mTitles.get(i).name;
                            final String id = mTitles.get(i).id;
                            mCustomTabLayout.addTab(mCustomTabLayout.newTab().setText(title));
                            mFragments[i] = DramaSelectFragment.newDramaSelectFragment(id);
                        }
                    }
                },throwable -> Toaster.showToast("error : " + throwable.getMessage()))
        );

    }

    class DramaSelectViewPager extends FragmentStatePagerAdapter {

        public DramaSelectViewPager(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override public CharSequence getPageTitle(int position) {
            return mTitles == null ? null : mTitles.get(position).name;
        }

        @Override public int getCount() {
            return mTitles == null ? 0 : mTitles.size();
        }
    }
}
