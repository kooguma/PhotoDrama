package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CustomTabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import com.laputapp.http.BaseResponse;
import com.laputapp.http.CacheResponse;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.CategoryService;
import com.loopeer.android.photodrama4android.api.service.SystemService;
import com.loopeer.android.photodrama4android.model.Advert;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.ui.fragment.DramaSelectFragment;
import com.loopeer.android.photodrama4android.utils.Toaster;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;

public class DramaSelectActivity extends PhotoDramaBaseActivity {

    private CustomTabLayout mCustomTabLayout;
    private ViewPager mViewPager;
    private List<Category> mTitles = new ArrayList<>();
    private List<List<Advert>> mAdverts = new ArrayList<>();
    private DramaSelectViewPagerAdapter mViewPagerAdapter;

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
        mViewPagerAdapter = new DramaSelectViewPagerAdapter(getSupportFragmentManager());

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
        registerSubscription(requestDisposable().subscribe());
    }

    private Flowable requestDisposable() {
        return Flowable.zip(
            CategoryService.INSTANCE.categories(CategoryService.TYPE_MODEL),
            SystemService.INSTANCE.listAd(),
            (categoryResponse, advertResponse) -> {
                List<Advert> adverts = advertResponse.mData;
                List<Category> categories = categoryResponse.mData;
                if (advertResponse.isSuccessed()) {
                    //sort adverts
                    String categoryId = null;
                    for (int j = 0; j < categories.size(); j++) {
                        if (!categories.get(j).id.equals(categoryId)) {
                            final List<Advert> ads = new ArrayList<>();
                            categoryId = categories.get(j).id;
                            for (Advert advert : adverts) {
                                if (advert.id.equals(categoryId)) {
                                    ads.add(advert);
                                }
                            }
                            mAdverts.add(ads);
                        }
                    }
                }
                if (categoryResponse.isSuccessed()) {
                    mTitles.clear();
                    mTitles.addAll(categories);
                    for (int i = 0; i < mTitles.size(); i++) {
                        final String title = mTitles.get(i).name;
                        mCustomTabLayout.addTab(mCustomTabLayout.newTab().setText(title));
                    }
                }
                return null;
            })
            .doOnTerminate(() -> {
                mViewPager.setAdapter(mViewPagerAdapter);
                mCustomTabLayout.setupWithViewPager(mViewPager);
            });
    }

    class DramaSelectViewPagerAdapter extends FragmentStatePagerAdapter {

        public DramaSelectViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            return DramaSelectFragment.newDramaSelectFragment
                (mTitles.get(position).id,
                (ArrayList<Advert>) mAdverts.get(position));
        }

        @Override public CharSequence getPageTitle(int position) {
            return mTitles == null ? null : mTitles.get(position).name;
        }

        @Override public int getCount() {
            return mTitles == null ? 0 : mTitles.size();
        }
    }
}
