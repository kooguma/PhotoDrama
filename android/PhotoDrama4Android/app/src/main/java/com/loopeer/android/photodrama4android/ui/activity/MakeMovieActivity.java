package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityMakeMovieBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;

import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class MakeMovieActivity extends MovieMakerBaseActivity implements VideoPlayerManager.ProgressChangeListener {

    private ActivityMakeMovieBinding mBinding;
    private VideoPlayerManager mVideoPlayerManager;
    private Drama mDrama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_make_movie);

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mBinding.seekBar), mBinding.glSurfaceView, mDrama);
        mVideoPlayerManager.setProgressChangeListener(this);
        mVideoPlayerManager.setStopTouchToRestart(true);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

        mBinding.glSurfaceView.setOnClickListener(v -> mVideoPlayerManager.pauseVideo());

        mVideoPlayerManager.onRestart();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.menu_save) {
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
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
        String hms = formatter.format(progress);
        mBinding.textStart.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1);
        mBinding.textTotal.setText(hmsTotal);
    }

    @Override
    public void onProgressStop() {
        mBinding.btnPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChange(int progress) {
        SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
        String hms = formatter.format(progress);
        mBinding.textStart.setText(hms);
    }

    @Override
    public void onProgressStart() {
        if (mBinding.btnPlay.getVisibility() == View.VISIBLE)
            mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onPlayBtnClick(View view) {
        mVideoPlayerManager.startVideo();
        mBinding.btnPlay.setVisibility(View.GONE);
    }

    public void onStartFragmentEdit(View view) {
        Navigator.startImageClipEditActivity(this, mDrama);
    }

    public void onFullBtnClick(View view) {
        Navigator.startFullLandscapePlayActivity(this, mDrama);
    }

    public void onTransitionEdit(View view) {
        Navigator.startTransitionEditActivity(this, mDrama);
    }

    public void onSubtitleEdit(View view) {
        Navigator.startSubtitleEditActivity(this, mDrama);
    }

    public void onAudioRecord(View view) {
        Navigator.startRecordMusicActivity(this, mDrama);
    }

    public void onSoundEffect(View view) {
        Navigator.startSoundEffectActivity(this, mDrama);
    }

    public void onBgmClick(View view) {
        Navigator.startBgmMusicActivity(this, mDrama);
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
                case Navigator.REQUEST_CODE_DRAMA_IMAGE_EDIT:
                case Navigator.REQUEST_CODE_DRAMA_TRANSITION_EDIT:
                case Navigator.REQUEST_CODE_DRAMA_SUBTITLE_EDIT:
                case Navigator.REQUEST_CODE_DRAMA_AUDIO_RECORD:
                case Navigator.REQUEST_CODE_DRAMA_SOUND_EFFECT:
                case Navigator.REQUEST_CODE_DRAMA_SOUND_BGM:
                    mVideoPlayerManager.updateDrama(mDrama);
                    break;
                default:
            }
            mVideoPlayerManager.seekToVideo(0);
        }
    }
}
