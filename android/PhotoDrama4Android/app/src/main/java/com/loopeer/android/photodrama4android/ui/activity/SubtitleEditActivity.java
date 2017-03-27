package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivitySubtitleEditBinding;
import com.loopeer.android.photodrama4android.opengl.Constants;
import com.loopeer.android.photodrama4android.opengl.SeekWrapper;
import com.loopeer.android.photodrama4android.opengl.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.opengl.VideoPlayerManager;
import com.loopeer.android.photodrama4android.opengl.model.Drama;
import com.loopeer.android.photodrama4android.opengl.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.opengl.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectInnderImageView;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;

public class SubtitleEditActivity extends MovieMakerBaseActivity {

    private ActivitySubtitleEditBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_subtitle_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mBinding.scrollSelectView)
                , mBinding.glSurfaceView, mDrama);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

        updateScrollImageView();
    }

    private void updateScrollImageView() {
        ScrollSelectView.Adapter<TransitionImageWrapper> adapter = new ScrollSelectView.Adapter<TransitionImageWrapper>() {

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.list_item_scroll_inner_item, parent, false);
            }

            @Override
            public void onBindView(View view, TransitionImageWrapper clip) {
                ScrollSelectInnderImageView innderImageView = (ScrollSelectInnderImageView) view;
                if (clip.isImageClip()) {
                    innderImageView.updateImage(Constants.DEFAULT_IMAGE_CLIP_SHOW_TIME, clip.imageClip.showTime, clip.imageClip.path);
                } else {
                    innderImageView.updateImage(Constants.DEFAULT_IMAGE_CLIP_SHOW_TIME, clip.transitionClip.showTime, clip.transitionPreImagePath);
                }
            }
        };
        mBinding.scrollSelectView.setAdapter(adapter);
        adapter.updateDatas(ClipsCreator.getTransiImageClipsNoEmpty(mDrama.videoGroup));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.menu_done) {
            Intent intent = new Intent();
            intent.putExtra(Navigator.EXTRA_DRAMA, mVideoPlayerManager.getDrama());
            setResult(RESULT_OK, intent);
            this.finish();
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
    }

    public void onPlayClick(View view) {
        /*mVideoPlayerManager.seekToVideo(mSelectedTransitionClip.startTime);
        mVideoPlayerManager.startVideoWithFinishTime(mSelectedTransitionClip.startTime);*/
    }

    public void onTextInputClick(View view) {
        Navigator.startTextInputActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Navigator.REQUEST_CODE_TEXT_INPUT:

                default:
            }
        }
    }
}
