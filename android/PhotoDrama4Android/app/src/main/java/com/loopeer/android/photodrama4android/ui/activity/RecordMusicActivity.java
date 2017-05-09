package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.loopeer.android.librarys.imagegroupview.utils.FileUtils;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityRecordMusicBinding;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.VideoPlayerManager;
import com.loopeer.android.photodrama4android.media.audio.AudioRecorder;
import com.loopeer.android.photodrama4android.media.model.Clip;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ScrollSelectAdapter;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.File;
import static com.loopeer.android.photodrama4android.media.model.MusicClip.MIN_RECORD_AUDIO_LENGTH;

public class RecordMusicActivity extends PhotoDramaBaseActivity implements VideoPlayerManager.ProgressChangeListener
        , ScrollSelectView.ClipIndicatorPosChangeListener, ScrollSelectView.ClipSelectedListener {

    private ActivityRecordMusicBinding mBinding;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;
    private AudioRecorder mAudioRecorder;
    private MusicClip mMusicClipRecording;
    private MusicClip mSelectedClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_record_music);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        mAudioRecorder = new AudioRecorder();
        mAudioRecorder.requestPermission(this);
        mVideoPlayerManager = new VideoPlayerManager(new SeekWrapper(mBinding.scrollSelectView)
                , mBinding.glSurfaceView, mDrama);
        mVideoPlayerManager.setProgressChangeListener(this);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);
        mVideoPlayerManager.seekToVideo(0);
        updateBtn();
        updateScrollImageView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateBtn() {
        mBinding.btnRecord.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    stopRecord(true);
                    break;
                case MotionEvent.ACTION_DOWN:
                    startRecord();
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    public void onDeleteClick(View view) {
        if (mSelectedClip != null) {
            mDrama.audioGroup.musicClips.remove(mSelectedClip);
            mVideoPlayerManager.getIMusic().updateDrama(mDrama);
            updateScrollSelectViewClips();
        }
    }

    private void stopRecord(boolean validate) {
        mAudioRecorder.stopRecording();
        mVideoPlayerManager.pauseVideo();
        if (mMusicClipRecording == null) return;

        mMusicClipRecording.setCreateIng(false);
        mMusicClipRecording.showTime = (int) (mVideoPlayerManager.getGLThread().getUsedTime() - mMusicClipRecording.startTime);
        mMusicClipRecording.musicSelectedLength = mMusicClipRecording.showTime;
        if (mMusicClipRecording.showTime < MIN_RECORD_AUDIO_LENGTH
                || !validate) {
            mDrama.audioGroup.musicClips.remove(mMusicClipRecording);
            FileUtils.deleteFile(new File(mMusicClipRecording.path));
        }

        mBinding.scrollSelectView.setProgress(mMusicClipRecording.getEndTime());
        mVideoPlayerManager.getIMusic().updateDrama(mDrama);
        mMusicClipRecording = null;
        updateScrollSelectViewClips();
    }

    private void startRecord() {
        mMusicClipRecording = new MusicClip((int) mVideoPlayerManager.getGLThread().getUsedTime()
                , MusicClip.MusicType.RECORD_AUDIO);
        mMusicClipRecording.path = FileManager.getInstance().createNewAudioFile();
        mMusicClipRecording.setCreateIng(true);
        if (!checkClipValidate(mMusicClipRecording)) {
            return;
        }
        mDrama.audioGroup.musicClips.add(mMusicClipRecording);
        updateScrollSelectViewClips();
        if (mAudioRecorder.startRecording(mMusicClipRecording)) {
            mVideoPlayerManager.startVideoOnly();
        }
    }

    private void updateScrollSelectViewClips() {
        mBinding.scrollSelectView.updateClips(mDrama.audioGroup.getRecordMusicClips());
    }

    private void updateScrollImageView() {
        mBinding.scrollSelectView.setClipIndicatorPosChangeListener(this);
        mBinding.scrollSelectView.setClipSelectedListener(this);
        mBinding.scrollSelectView.setMinClipShowTime(MIN_RECORD_AUDIO_LENGTH);
        ScrollSelectView.Adapter<TransitionImageWrapper> adapter = new ScrollSelectAdapter();
        mBinding.scrollSelectView.setAdapter(adapter);
        adapter.updateDatas(ClipsCreator.getTransiImageClipsNoEmpty(mDrama.videoGroup));
        updateScrollSelectViewClips();
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
        mAudioRecorder.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayManagerContainer.getDefault().onFinish(this);
        mVideoPlayerManager.onDestroy();
    }

    public void onPlayClick(View view) {
        mVideoPlayerManager.startVideo();
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {

    }

    @Override
    public void onProgressStop() {

    }

    @Override
    public void onProgressChange(int progress) {
        if (mMusicClipRecording != null) {
            mMusicClipRecording.showTime = (int) (mVideoPlayerManager.getGLThread().getUsedTime() - mMusicClipRecording.startTime);
            if (!checkClipValidate(mMusicClipRecording)) {
                stopRecord(false);
            }
        }
    }

    @Override
    public void onProgressStart() {

    }

    @Override
    public boolean changeTimeByStartIndicator(Clip clip, int offset, int minValue, int maxValue) {
        changeClipTimeByIndicator(clip, offset, maxValue);
        return true;
    }

    @Override
    public boolean changeTimeByEndIndicator(Clip clip, int offset, int minValue, int maxValue) {
        changeClipTimeByIndicator(clip, offset, maxValue);
        return true;
    }

    private void changeClipTimeByIndicator(Clip clip, int offset, int maxValue) {
        int preStartTime = clip.startTime;
        clip.startTime += offset;
        if (clip.getEndTime() >= maxValue + 1)
            clip.startTime = maxValue + 1 - clip.showTime;
        if (clip.startTime <= 0) {
            clip.startTime = 0;
        }
        for (MusicClip c : mDrama.audioGroup.getRecordMusicClips()) {
            if (clip != c) {
                if (preStartTime < c.startTime && clip.getEndTime() >= c.startTime) {
                    clip.startTime = c.startTime - 1 - clip.showTime;
                    break;
                }
                if (preStartTime > c.startTime && clip.startTime <= c.getEndTime()) {
                    clip.startTime = c.getEndTime() + 1;
                    break;
                }
            }
        }
    }

    private boolean checkClipValidate(Clip recordingClip) {
        if (recordingClip.startTime + MusicClip.MIN_RECORD_AUDIO_LENGTH > mVideoPlayerManager.getMaxTime())
            return false;
        if (recordingClip.getEndTime() > mVideoPlayerManager.getMaxTime())
            return false;
        for (MusicClip clip : mDrama.audioGroup.getRecordMusicClips()) {
            if (recordingClip != clip) {
                if (recordingClip.startTime < clip.startTime
                        && recordingClip.startTime + MusicClip.MIN_RECORD_AUDIO_LENGTH >= clip.startTime)
                    return false;
                if (recordingClip.startTime < clip.startTime
                        && recordingClip.getEndTime() >= clip.startTime)
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onClipSelected(Clip clip) {
        if (clip != null) {
            mSelectedClip = (MusicClip) clip;
            mBinding.switcherBtn.setDisplayedChild(1);
        } else {
            mSelectedClip = null;
            mBinding.switcherBtn.setDisplayedChild(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAudioRecorder.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
