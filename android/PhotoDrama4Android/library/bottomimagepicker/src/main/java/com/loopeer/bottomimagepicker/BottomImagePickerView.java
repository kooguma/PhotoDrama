package com.loopeer.bottomimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

public class BottomImagePickerView extends LinearLayout {

    private static final String STATE = "state";
    private static final String TAB_INDEX = "tab_index";

    private static final int LOADER_ID_FOLDER = 10001;

    private ImageView mIcon;
    private TabLayout mTabLayout;
    private SlideCustomViewPager mViewPager;

    private List<String> mTitles;
    private PickerFragmentAdapter mFragmentAdapter;

    private LoaderManagerImpl mLoaderManager;

    private ImageAdapter.OnImagePickListener mOnImagePickListener;

    private String mPath;
    private int mCurTabIndex = -1;

    public void setOnImagePickListener(ImageAdapter.OnImagePickListener listener) {
        this.mOnImagePickListener = listener;
    }

    public BottomImagePickerView(Context context) {
        super(context);
    }

    public BottomImagePickerView(Context context,
                                 @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomImagePickerView(Context context,
                                 @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(getContext())
            .inflate(R.layout.view_bottom_image_picker, this, true);
        mIcon = (ImageView) view.findViewById(R.id.icon);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (SlideCustomViewPager) view.findViewById(R.id.view_pager);

        mTitles = new ArrayList<>();
        mLoaderManager = new LoaderManagerImpl(this);
        mFragmentAdapter = new PickerFragmentAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setSlideEnable(true);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                mCurTabIndex = tab.getPosition();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID_FOLDER, null, mLoaderManager);

    }

    @Override protected void onRestoreInstanceState(Parcelable state) {
        if (state != null && state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            final int index = bundle.getInt(TAB_INDEX);
            state = bundle.getParcelable(STATE);
            mTabLayout.post(new Runnable() {
                @Override public void run() {
                    TabLayout.Tab tab = mTabLayout.getTabAt(index);
                    if (tab != null && mCurTabIndex != -1) {
                        tab.select();
                    }
                }
            });

        }
        super.onRestoreInstanceState(state);
    }

    @Override protected Parcelable onSaveInstanceState() {
        final int index = mCurTabIndex;
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE, super.onSaveInstanceState());
        bundle.putInt(TAB_INDEX, index);
        return bundle;
    }

    public void setData(List<ImageFolder> folders) {
        mTitles.clear();
        for (int i = 0; i < folders.size(); i++) {
            ImageFolder folder = folders.get(i);
            mTitles.add(folder.name);
        }
        mFragmentAdapter.updateImageFolders(folders);
    }

    private LoaderManager getSupportLoaderManager() {
        Activity activity = getActivity();
        if (activity == null || !(activity instanceof FragmentActivity)) {
            throw new IllegalStateException("check out the activity.is it a FragmentActivity ?");
        } else {
            return ((FragmentActivity) activity).getSupportLoaderManager();
        }
    }

    private FragmentManager getSupportFragmentManager() {
        Activity activity = getActivity();
        if (activity == null || !(activity instanceof FragmentActivity)) {
            throw new IllegalStateException("check out the activity.is it a FragmentActivity ?");
        } else {
            return ((FragmentActivity) activity).getSupportFragmentManager();
        }
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public int getPeekHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return PickerFragment.getUnitSize(wm) * PickerFragment.IMAGE_SIZE_UNIT +
            mTabLayout.getHeight() + mIcon.getHeight();
    }

    public SlideCustomViewPager getViewPager() {
        return mViewPager;
    }

    public ImageView getIconView() {
        return mIcon;
    }

    public View getCurrentRecyclerView(int position) {
        return mFragmentAdapter.getFragment(position).getView().findViewById(R.id.recycler_picker);
    }

    public void updateSelectedImage(String path) {
        PickerFragment fragment = (PickerFragment) mFragmentAdapter.getFragment(mCurTabIndex);
        fragment.updateSelectedImage(path);
    }

    private class PickerFragmentAdapter extends PickerFragmentStatePagerAdapter {

        private List<ImageFolder> mImageFolders;

        public PickerFragmentAdapter(FragmentManager fm) {
            super(fm);
            mImageFolders = new ArrayList<>();
        }

        public void updateImageFolders(List<ImageFolder> imageFolders) {
            mImageFolders.clear();
            mImageFolders.addAll(imageFolders);
            notifyDataSetChanged();
        }

        @Override public Fragment getItem(int position) {
            return PickerFragment.newInstance(mImageFolders.get(position).images, mOnImagePickListener);
        }

        @Override public int getCount() {
            return mTitles == null ? 0 : mTitles.size();
        }

        @Override public CharSequence getPageTitle(int position) {
            return mTitles == null ? null : mTitles.get(position);
        }
    }

}
