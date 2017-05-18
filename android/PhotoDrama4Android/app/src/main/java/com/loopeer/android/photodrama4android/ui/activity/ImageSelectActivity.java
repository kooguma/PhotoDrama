package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityImageSelectBinding;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.ui.adapter.ImageSelectedAdapter;
import com.loopeer.bottomimagepicker.ImageAdapter;
import com.loopeer.bottomimagepicker.PickerBottomBehavior;

import java.io.File;
import java.util.List;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class ImageSelectActivity extends PhotoDramaBaseActivity implements ImageSelectedAdapter.OnImageSelectedListener {

    private ActivityImageSelectBinding mBinding;
    private ImageSelectedAdapter mImageSelectedAdapter;

    private  ImageAdapter.OnImagePickListener mPickListener
        = new ImageAdapter.OnImagePickListener() {
        @Override public boolean onImagePick(Uri uri) {
            mImageSelectedAdapter.addUri(uri);
            return true;
        }
    };

    private void updateDisplayImage(String path) {
        mBinding.imageDisplay.setImageURI(path == null ? null : Uri.fromFile(new File(path)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_select);
        setupView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        setCenterTitle(R.string.label_drama_edit);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_make, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_make) {
            List<String> urls = mImageSelectedAdapter.getUrls();
            if (urls.size() < 2) {
                showToast(R.string.image_too_small);
                return true;
            }
            Navigator.startMakeMovieActivity(ImageSelectActivity.this,
                    Drama.createFromPath(urls));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        PickerBottomBehavior behavior = PickerBottomBehavior.from(mBinding.pickView);
        mBinding.pickView.setOnImagePickListener(mPickListener);
        mBinding.pickView.getViewPager()
                .addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        behavior.updateNestScrollChild(
                                mBinding.pickView.getCurrentRecyclerView(position));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

        updateSegmentList();

        mBinding.pickView.post(() -> {
            int containerHeight = mBinding.container.getHeight();
            int recyclerBottom = mBinding.recyclerView.getBottom();
            int minSheetHeight = containerHeight - recyclerBottom;
            behavior.setPeekHeight(minSheetHeight);
        });
        mBinding.imageDisplay.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mBinding.imageDisplay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int containerHeight = mBinding.container.getHeight();
                        int recyclerBottom = mBinding.recyclerView.getBottom();
                        int minSheetHeight = containerHeight - recyclerBottom;
                        behavior.setPeekHeight(minSheetHeight);
                    }
                });
    }

    private void updateSegmentList() {
        mImageSelectedAdapter = new ImageSelectedAdapter(this);
        mImageSelectedAdapter.setOnSelectedListener(this);
        mBinding.recyclerView.setPadding(13, 0, 13, 0);
        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerView.setAdapter(mImageSelectedAdapter);
        mBinding.recyclerView.setItemViewCacheSize(6);
        mImageSelectedAdapter.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onImageClipSelected(ImageClip imageClip) {
        updateDisplayImage(imageClip == null ? null : imageClip.path);
        mBinding.pickView.updateSelectedImage(imageClip == null ? null : imageClip.path);
    }
}
