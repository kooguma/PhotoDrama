package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityDramaEditBinding;
import com.loopeer.android.photodrama4android.media.HandlerWrapper;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.utils.DramaFetchHelper;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.adapter.EditDramaSegmentAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.ImageSegmentAdapter;
import com.loopeer.android.photodrama4android.ui.widget.GalleryLinearLayout;
import com.loopeer.bottomimagepicker.BottomImagePickerView;
import com.loopeer.bottomimagepicker.ImageAdapter;
import com.loopeer.bottomimagepicker.PickerBottomBehavior;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaEditActivity extends MovieMakerBaseActivity implements EditDramaSegmentAdapter.OnSelectedListener
        , VideoPlayerManager.BitmapReadyListener, VideoPlayerManager.ProgressChangeListener {

    private ActivityDramaEditBinding mBinding;
    private ImageView mIcon;
    private BottomImagePickerView mBottomImagePickerView;
    private Theme mTheme;
    private EditDramaSegmentAdapter mEditDramaSegmentAdapter;
    private VideoPlayerManager mVideoPlayerManager;
    private Drama mDrama;
    private DramaFetchHelper mDramaFetchHelper;
    private ImageClip mSelectedImageClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_drama_edit);

        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);
        setupView();
        loadDrama();
    }

    private void loadDrama() {
        if (mTheme == null) return;
        showProgressLoading("");
        mDramaFetchHelper = new DramaFetchHelper(this);
        mDramaFetchHelper.getDrama(mTheme,
                drama -> {
                    updateDrama(drama);
                }, throwable -> {
                    throwable.printStackTrace();
                    showToast(throwable.toString());
                }, () -> {
                    dismissProgressLoading();
                });
    }

    private void updateDrama(Drama drama) {
        mDrama = drama;
        mVideoPlayerManager.updateDrama(mDrama);
        mEditDramaSegmentAdapter.updateData(mDrama.videoGroup.imageClips);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
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

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        mVideoPlayerManager = new VideoPlayerManager(null,
                mBinding.glSurfaceView, new Drama());
        mVideoPlayerManager.setBitmapReadyListener(this);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.setProgressChangeListener(this);
        mVideoPlayerManager.seekToVideo(0);

        mBinding.glSurfaceView.setOnClickListener(v -> {
            if (mVideoPlayerManager.isStop())
                mVideoPlayerManager.startVideo();
            else
                mVideoPlayerManager.pauseVideo();
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
                        , t -> VideoPlayManagerContainer.getDefault().bitmapLoadReady(DramaEditActivity.this
                        , mSelectedImageClip.path));
                mBinding.glSurfaceView.getTextureLoader().loadImageTexture(handler);
                return false;
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
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
    }

    @Override
    public void bitmapReady(String path) {
        mEditDramaSegmentAdapter.onBitmapReady(path);
    }

    public void onFullBtnClick(View view) {
        Navigator.startFullLandscapePlayActivity(this, mDrama);
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
}
