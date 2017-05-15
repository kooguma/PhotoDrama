package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CustomTabLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.ui.fragment.AddMusicClipFragment;
import com.loopeer.android.photodrama4android.ui.fragment.DramaSelectFragment;

public class AddMusicClipActivity extends PhotoDramaBaseActivity {

    private CustomTabLayout mTabLayout;
    private ViewPager mViewPager;

    private static final String[] sTitle = { "我的下载", "精选推荐" };
    private AddMusicClipFragment[] mFragments = new AddMusicClipFragment[2];

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music_clip);

        mTabLayout = (CustomTabLayout) findViewById(R.id.music_clip_add_tab);
        mViewPager = (ViewPager) findViewById(R.id.music_clip_add_view_pager);

        mFragments[0] = AddMusicClipFragment.newInstance();
        mFragments[1] = AddMusicClipFragment.newInstance();

        mTabLayout.addTab(mTabLayout.newTab().setText(sTitle[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(sTitle[1]));

        mViewPager.setAdapter(new AddMusicClipPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setCenterTitle(R.string.label_add_music_bgm);
    }

    class AddMusicClipPagerAdapter extends FragmentPagerAdapter {

        public AddMusicClipPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            return mFragments == null ? null : mFragments[position];
        }

        @Override public CharSequence getPageTitle(int position) {
            return sTitle == null ? null : sTitle[position];
        }

        @Override public int getCount() {
            return sTitle == null ? 0 : sTitle.length;
        }
    }

}
