package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CustomTabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.ui.fragment.MyDownloadMusicFragment;
import com.loopeer.android.photodrama4android.ui.fragment.RecommendMusicFragment;

public class AddMusicClipActivity extends PhotoDramaBaseActivity {

    private CustomTabLayout mTabLayout;
    private ViewPager mViewPager;

    private static final String[] sTitle = { "我的下载", "精选推荐" };
    private Fragment[] mFragments = new Fragment[2];

    private MusicClip.MusicType mType;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music_clip);

        mType = (MusicClip.MusicType) getIntent().getSerializableExtra(
            Navigator.EXTRA_MUSIC_CLIP);

        mTabLayout = (CustomTabLayout) findViewById(R.id.music_clip_add_tab);
        mViewPager = (ViewPager) findViewById(R.id.music_clip_add_view_pager);

        mFragments[0] = MyDownloadMusicFragment.newInstance(mType);
        mFragments[1] = RecommendMusicFragment.newInstance(mType);

        mTabLayout.addTab(mTabLayout.newTab().setText(sTitle[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(sTitle[1]));

        mViewPager.setAdapter(new AddMusicClipPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

    }

    public void switchToRecommend() {
        CustomTabLayout.Tab tab = mTabLayout.getTabAt(1);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final int titleRes = mType == MusicClip.MusicType.BGM
                             ? R.string.label_add_music_bgm
                             : R.string.label_add_music_effect;
        setCenterTitle(titleRes);
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
