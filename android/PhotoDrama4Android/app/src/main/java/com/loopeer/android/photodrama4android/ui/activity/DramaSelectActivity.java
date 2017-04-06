package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.CategoryService;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.ui.fragment.DramaSelectFragment;
import com.loopeer.android.photodrama4android.utils.Toaster;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import rx.functions.Action0;
import rx.functions.Action1;

public class DramaSelectActivity extends MovieMakerBaseActivity {

    private TabLayout mTabLayout;
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
        mTabLayout = (TabLayout) findViewById(R.id.drama_select_tab_select);
        mViewPager = (ViewPager) findViewById(R.id.drama_select_view_pager);

        registerSubscription(
            ResponseObservable.unwrap(this, CategoryService.INSTANCE.categories())
                .doOnTerminate(() -> {
                    mViewPager.setAdapter(
                        new DramaSelectViewPager(getSupportFragmentManager()));
                    mTabLayout.setupWithViewPager(mViewPager);
                })
                .subscribe(categories -> {
                    if (categories != null && !categories.isEmpty()) {
                        mTitles.clear();
                        mTitles.addAll(categories);
                        mFragments = new DramaSelectFragment[categories.size()];
                        for (int i = 0; i < mTitles.size(); i++) {
                            final String title = mTitles.get(i).name;
                            final String id = mTitles.get(i).id;
                            mTabLayout.addTab(mTabLayout.newTab().setText(title));
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
