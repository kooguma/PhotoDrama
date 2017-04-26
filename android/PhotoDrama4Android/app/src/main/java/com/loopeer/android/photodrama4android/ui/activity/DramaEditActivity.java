package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.facebook.common.util.UriUtil;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaEditBinding;
import com.loopeer.android.photodrama4android.media.HandlerWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.utils.DramaFetchHelper;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.adapter.EditDramaSegmentAdapter;
import com.loopeer.android.photodrama4android.ui.widget.loading.ExportLoadingDialog;
import com.loopeer.android.photodrama4android.ui.hepler.ILoader;
import com.loopeer.android.photodrama4android.ui.hepler.ThemeLoader;
import com.loopeer.bottomimagepicker.BottomImagePickerView;
import com.loopeer.bottomimagepicker.ImageAdapter;
import com.loopeer.bottomimagepicker.PickerBottomBehavior;
import java.io.File;

public class DramaEditActivity extends PhotoDramaBaseActivity
    implements EditDramaSegmentAdapter.OnSelectedListener
    ,
    VideoPlayerManager.BitmapReadyListener,
    VideoPlayerManager.ProgressChangeListener,
    VideoPlayerManager.RecordingListener {

    private ActivityDramaEditBinding mBinding;
    private ImageView mIcon;
    private BottomImagePickerView mBottomImagePickerView;
    private Theme mTheme;
    private EditDramaSegmentAdapter mEditDramaSegmentAdapter;
    private VideoPlayerManager mVideoPlayerManager;
    private Drama mDrama;
    private ILoader mLoader;
    private DramaFetchHelper mDramaFetchHelper;
    private ImageClip mSelectedImageClip;
    private ExportLoadingDialog mExportProgressLoading;
    private int mUsedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_drama_edit);

        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);
        Analyst.dramaUseClick(mTheme.id);
        setupView();
        loadDrama();
    }

    private void loadDrama() {
        if (mTheme == null) return;
        mLoader.showProgress();
        mDramaFetchHelper = new DramaFetchHelper(this);
        mDramaFetchHelper.getDrama(mTheme,
            drama -> {
                updateDrama(drama);
            }, throwable -> {
                throwable.printStackTrace();
                mLoader.showMessage(throwable.getMessage());
            }, () -> {
                mLoader.showContent();
            });
    }

    private void updateDrama(Drama drama) {
        mDrama = drama;
        mVideoPlayerManager.updateDrama(mDrama);
        mVideoPlayerManager.seekToVideo(mUsedTime);
        mEditDramaSegmentAdapter.updateData(mDrama.videoGroup.imageClips);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
        setCenterTitle(R.string.label_drama_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.menu_export) {
            Analyst.downloadClick();
            mVideoPlayerManager.startRecording();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        mLoader = new ThemeLoader(mBinding.animator);
        mVideoPlayerManager = new VideoPlayerManager(null,
            mBinding.glSurfaceView, new Drama());
        mVideoPlayerManager.setBitmapReadyListener(this);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.setProgressChangeListener(this);
        mVideoPlayerManager.seekToVideo(0);
        mVideoPlayerManager.setRecordingListener(this);

        mBinding.glSurfaceView.setOnClickListener(v -> {
            if (mVideoPlayerManager.isStop()) {
                mVideoPlayerManager.startVideo();
            } else {
                mVideoPlayerManager.pauseVideo();
            }
        });
        mBottomImagePickerView = (BottomImagePickerView) findViewById(R.id.pick_view);
        mIcon = mBottomImagePickerView.getIconView();

        PickerBottomBehavior behavior = PickerBottomBehavior.from(mBottomImagePickerView);
        behavior.setBottomSheetCallback(new PickerBottomBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(
                @NonNull View bottomSheet, @PickerBottomBehavior.State int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                final float degrees = slideOffset * 180;
                mIcon.setRotation(degrees);
            }
        });
        mBottomImagePickerView.setOnImagePickListener(new ImageAdapter.OnImagePickListener() {
            @Override
            public boolean onImagePick(Uri uri) {
                BitmapFactory.getInstance().removeBitmapToCache(mSelectedImageClip.path);
                mSelectedImageClip.path = uri.getPath();
                HandlerWrapper handler = new HandlerWrapper(
                    Looper.getMainLooper(),
                    HandlerWrapper.TYPE_LOAD_IMAGE
                    , mSelectedImageClip.path
                    , t -> VideoPlayManagerContainer.getDefault()
                    .bitmapLoadReady(DramaEditActivity.this
                        , mSelectedImageClip.path));
                mBinding.glSurfaceView.getTextureLoader().loadImageTexture(handler);
                return true;
            }
        });
        mBottomImagePickerView.getViewPager()
            .addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    behavior.updateNestScrollChild(
                        mBottomImagePickerView.getCurrentRecyclerView(position));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        updateSegmentList();

        mBinding.glSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mBinding.glSurfaceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int containerHeight = mBinding.container.getHeight();
                    int recyclerBottom = mBinding.recyclerView.getBottom();
                    int minSheetHeight = containerHeight - recyclerBottom;
                    behavior.setPeekHeight(minSheetHeight);
                }
            });
    }

    private void updateSegmentList() {
        mEditDramaSegmentAdapter = new EditDramaSegmentAdapter(this);
        mEditDramaSegmentAdapter.setOnSelectedListener(this);
        mBinding.recyclerView.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerView.setAdapter(mEditDramaSegmentAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mVideoPlayerManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoPlayerManager.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoPlayerManager.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayManagerContainer.getDefault().onFinish(this);
        mVideoPlayerManager.onDestroy();
        mDramaFetchHelper.unSubscribe();
        BitmapFactory.getInstance().clear();
    }

    @Override
    public void onImageSelected(ImageClip imageClip) {
        mSelectedImageClip = imageClip;
        mVideoPlayerManager.seekToVideo(mSelectedImageClip.startTime);
        mBottomImagePickerView.updateSelectedImage(mSelectedImageClip.path);
    }

    @Override
    public void bitmapReady(String path) {
        mEditDramaSegmentAdapter.onBitmapReady(path);
    }

    public void onFullBtnClick(View view) {
        Navigator.startFullLandscapePlayActivityForResult(this, mDrama,
            mVideoPlayerManager.isStop(),
            mVideoPlayerManager.getUsedTime());
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {

    }

    @Override
    public void onProgressStop() {
        mBinding.btnPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChange(int progress) {
    }

    @Override
    public void onProgressStart() {
        mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void showExportProgress(String message) {
        if (mExportProgressLoading == null) {
            mExportProgressLoading = new ExportLoadingDialog(this,
                R.style.ExportProgressLoadingTheme);
            mExportProgressLoading.setCanceledOnTouchOutside(false);
            mExportProgressLoading.setCancelable(false);
        }
        if (!TextUtils.isEmpty(message)) {
            mExportProgressLoading.setMessage(message);
        } else {
            mExportProgressLoading.setMessage(null);
        }
        mExportProgressLoading.show();
    }

    public void dismissExportProgressLoading() {
        if (mExportProgressLoading != null && !isFinishing()) {
            mExportProgressLoading.dismiss();
        }
    }

    @Override
    public void recordStart() {
        mBinding.viewCover.setVisibility(View.VISIBLE);
        showExportProgress(getString(R.string.drama_export_message));
    }

    @Override
    public void recordChange(int progress) {
        if (mExportProgressLoading != null) {
            mExportProgressLoading.setProgress(
                1f * progress / (mVideoPlayerManager.getSeekbarMaxValue() + 1));
        }
    }

    @Override
    public void recordFinished(String path) {
        mBinding.viewCover.setVisibility(View.GONE);
        dismissExportProgressLoading();
        Navigator.startShareActivity(this, path);
    }

    @Override
    public void onBackPressed() {
        if (mVideoPlayerManager.isRecording()) return;
        super.onBackPressed();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Navigator.REQUEST_FULL_SCREEN) {
            boolean restart = data.getBooleanExtra(Navigator.EXTRA_IS_TO_START, false);
            int usedTime = data.getIntExtra(Navigator.EXTRA_USEDTIME, -1);
            if (usedTime != -1) {
                mUsedTime = usedTime;
                if (mVideoPlayerManager != null) {
                    mVideoPlayerManager.seekToVideo(mUsedTime);
                    if (restart) {
                        mVideoPlayerManager.startVideo();
                    }
                }
            }
        }
    }
}
