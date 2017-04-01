package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.ui.fragment.DramaSelectFragment;
import java.util.List;

public class DramaSelectActivity extends MovieMakerBaseActivity {

    private static final int sDefaultCount = 8;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private DramaSelectFragment[] mFragments;
    private String[] mTitles;

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
        mTabLayout = (TabLayout) findViewById(R.id.drama_select_tab_select);
        mViewPager = (ViewPager) findViewById(R.id.drama_select_view_pager);

        mFragments = new DramaSelectFragment[sDefaultCount];
        mTitles = getResources().getStringArray(R.array.drama_category);

        for (int i = 0; i < sDefaultCount; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitles[i]));
            mFragments[i] = new DramaSelectFragment();
        }

        mViewPager.setAdapter(new DramaSelectViewPager(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

    }

    class DramaSelectViewPager extends FragmentStatePagerAdapter {

        public DramaSelectViewPager(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override public CharSequence getPageTitle(int position) {
            return mTitles == null ? null : mTitles[position];
        }

        @Override public int getCount() {
            return mTitles == null ? 0 : mTitles.length;
        }
    }
}
