package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.databinding.ActivityMakeMovieBinding;
import com.loopeer.android.photodrama4android.databinding.ViewMakeMovieEditItemBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;
import com.loopeer.android.photodrama4android.model.DramaMakeItem;
import com.loopeer.android.photodrama4android.ui.hepler.DramaEditOrientationAdapter;
import com.loopeer.android.photodrama4android.ui.hepler.MakeMovieOrientationAdapter;
import com.loopeer.android.photodrama4android.ui.hepler.ScreenOrientationHelper;
import com.loopeer.android.photodrama4android.ui.widget.loading.ExportLoadingDialog;

import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.loopeer.android.photodrama4android.media.utils.DateUtils.formatTime;
import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class MakeMovieActivity extends PhotoDramaBaseActivity implements VideoPlayerManager.ProgressChangeListener, VideoPlayerManager.RecordingListener {

    private ActivityMakeMovieBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private Drama mDrama;
    private ExportLoadingDialog mExportProgressLoading;

    private ScreenOrientationHelper mScreenOrientationHelper;
    private MakeMovieOrientationAdapter mOrientationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_make_movie);

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mVideoPlayerManager = new VideoPlayerManager(mBinding.glSurfaceView, mDrama
                , new SeekWrapper(mBinding.seekBar), new SeekWrapper(mBinding.viewFullBottom.seekBar));
        mVideoPlayerManager.addProgressChangeListener(this);
        mVideoPlayerManager.setStopTouchToRestart(true);
        mVideoPlayerManager.setRecordingListener(this);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

        mVideoPlayerManager.onRestart();
        setUpEditItem();

        mOrientationAdapter = new MakeMovieOrientationAdapter(mBinding, this);
        mScreenOrientationHelper = new ScreenOrientationHelper(this
                , getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                , mOrientationAdapter);
        mOrientationAdapter.setVideoPlayerManager(mVideoPlayerManager);
        mOrientationAdapter.onCreate();
    }

    private void setUpEditItem() {
        mBinding.containerEditItem.removeAllViews();
        for (DramaMakeItem item :
                DramaMakeItem.sDramaMakeItems) {
            ViewMakeMovieEditItemBinding binding = DataBindingUtil.inflate(getLayoutInflater()
                    , R.layout.view_make_movie_edit_item, mBinding.containerEditItem, true);
            binding.imgIcon.setImageResource(item.icon);
            binding.textTitle.setText(item.text);
            binding.getRoot().setOnClickListener(v
                    -> {
                Analyst.logEvent(item.analystEventKey);
                Navigator.startDramaEditItemActivity(MakeMovieActivity.this, mDrama, item.targetClass);
            });
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_up_white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mOrientationAdapter.update(menu.findItem(R.id.menu_export));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        if (item.getItemId() == R.id.menu_export) {
            Analyst.myCreatDownloadClick();
            mVideoPlayerManager.startRecording();
        }
        return super.onOptionsItemSelected(item);
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
        BitmapFactory.getInstance().clear();
        mOrientationAdapter.onDestroy();
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {
        mBinding.textStart.setText(formatTime(progress));
        mBinding.textTotal.setText(formatTime(maxValue + 1));
    }

    @Override
    public void onProgressStop() {
        mBinding.btnPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        mBinding.textStart.setText(formatTime(progress));
    }

    @Override
    public void onProgressStart() {
        if (mBinding.btnPlay.getVisibility() == View.VISIBLE)
            mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onPlayBtnClick(View view) {
        Analyst.myStarPlayClick();
        mVideoPlayerManager.startVideo();
        mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onFullBtnClick(View view) {
        mScreenOrientationHelper.fullScreen();
    }

    public void onCreateZip(View view) {
        mVideoPlayerManager.pauseVideo();
        showProgressLoading("");

        Flowable.fromCallable(new Callable<Drama>() {
            @Override
            public Drama call() throws Exception {
                ZipUtils.zipFile(mDrama);
                return null;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    dismissProgressLoading();
                    showToast(R.string.drama_make_zip_success);
                })
                .subscribe();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Drama drama = (Drama) data.getSerializableExtra(Navigator.EXTRA_DRAMA);
            if (drama != null)
                mDrama = drama;
            switch (requestCode) {
                case Navigator.REQUEST_CODE_DRAMA_MAKE_EDIT:
                    mVideoPlayerManager.updateDrama(mDrama);
                    if (mVideoPlayerManager.getMaxTime() < mVideoPlayerManager.getUsedTime())
                        mVideoPlayerManager.seekToVideo(0);
                    break;
                default:
            }
        }
    }

    @Override
    public void onBackPressed() {
        mScreenOrientationHelper.backPressed();
    }

    @Override
    public void recordStart() {
        mBinding.viewCover.setVisibility(View.VISIBLE);
        showExportProgress(getString(R.string.drama_export_message));
    }

    @Override
    public void recordChange(int progress) {
        if (mExportProgressLoading != null) {
            mExportProgressLoading.setProgress(1f * progress / (mVideoPlayerManager.getSeekbarMaxValue() + 1));
        }
    }

    @Override
    public void recordFinished(String path) {
        mBinding.viewCover.setVisibility(View.GONE);
        dismissExportProgressLoading();
        Navigator.startShareActivity(this, path);
    }

    public void showExportProgress(String message) {
        if (mExportProgressLoading == null) {
            mExportProgressLoading = new ExportLoadingDialog(this, R.style.ExportProgressLoadingTheme);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mScreenOrientationHelper.updateOrientation(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

}
