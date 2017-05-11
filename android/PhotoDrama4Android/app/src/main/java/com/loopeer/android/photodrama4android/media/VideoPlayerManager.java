package com.loopeer.android.photodrama4android.media;

import android.content.Context;
import android.util.Log;
import android.view.TextureView;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.audio.MusicDelegate;
import com.loopeer.android.photodrama4android.media.audio.MusicProcessor;
import com.loopeer.android.photodrama4android.media.audio.player.AudioDelegate;
import com.loopeer.android.photodrama4android.media.audio.player.AudioProcessor;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.EndLogoClip;
import com.loopeer.android.photodrama4android.media.render.GLRenderWorker;
import com.loopeer.android.photodrama4android.media.render.GLThreadRender;
import com.loopeer.android.photodrama4android.utils.FileManager;

import static com.loopeer.android.photodrama4android.utils.FileManager.scanIntoMediaStore;

public class VideoPlayerManager
        implements OnSeekProgressChangeListener, SeekChangeListener, IPlayerLife,
        AudioProcessor.AudioProcessorPrepareListener, MusicProcessor.ProcessorPrepareListener {

    private static final String TAG = "VideoPlayerManager";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    private SeekWrapper mSeekWrapper;
    private GLThreadRender mGLThread;
    private ProgressChangeListener mProgressChangeListener;
    private RecordingListener mRecordingListener;
    private GLRenderWorker mGLRenderWorker;
    private int mSeekbarMaxValue;
    private IMusic mIMusic;
    private Context mContext;
    private int mStartTime;
    private int mMaxTime;
    private int mEndTime;
    private int mFinishAtTime;
    private boolean mIsStopTouchToRestart;
    private boolean isMusicPrepared = false;
    private boolean isImagePrepared = false;
    private boolean isSubtitlePrepared = false;
    private boolean mIsRecording;
    private BitmapReadyListener mBitmapReadyListener;

    public VideoPlayerManager(SeekWrapper seekWrapper, TextureView glSurfaceView, Drama drama) {
        mContext = glSurfaceView.getContext();
        mSeekWrapper = seekWrapper;
        mGLRenderWorker = new GLRenderWorker(mContext, drama, glSurfaceView);
        mGLThread = new GLThreadRender(glSurfaceView.getContext(), glSurfaceView, mGLRenderWorker);

        if (BuildConfig.DEBUG) {
            mIMusic = new AudioDelegate(mContext, drama, this);
        } else {
            mIMusic = new MusicDelegate(mContext, drama, this);
        }

        updateTime(drama);
        init();
    }

    private void updateTime(Drama drama) {
        mMaxTime = drama.getShowTimeTotal();
        setSeekBarMaxValue(mMaxTime);
        if (mSeekWrapper != null) mSeekWrapper.setMax(mMaxTime);
        mStartTime = 0;
        mFinishAtTime = mStartTime;
        mEndTime = mMaxTime;
        mGLThread.updateTime(mStartTime, mEndTime);
        onProgressInit(mStartTime, mSeekbarMaxValue);
    }

    private void init() {
        if (mSeekWrapper != null) mSeekWrapper.setOnSeekChangeListener(this);
        mGLThread.setSeekChangeListener(this);
    }

    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        mProgressChangeListener = progressChangeListener;
        onProgressInit(mStartTime, mSeekbarMaxValue);
    }

    public void setRecordingListener(RecordingListener recordingListener) {
        this.mRecordingListener = recordingListener;
    }

    public void setSeekBarMaxValue(int seekBarMaxValue) {
        mSeekbarMaxValue = seekBarMaxValue;
    }

    @Override
    public void onProgressChanged(SeekWrapper.SeekImpl seek, int progress, boolean fromUser) {
        if (mGLThread == null) return;
        if (fromUser) seekToVideo(progress);
        onProgressChange(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekWrapper.SeekImpl seek) {
        mGLThread.stopUp();
        mGLThread.setManual(true);
        mIMusic.pauseMusic();
    }

    @Override
    public void onStopTrackingTouch(SeekWrapper.SeekImpl seek) {
        mGLThread.setUsedTime(seek.getProgress());
        if (mIsStopTouchToRestart) stopTouchToRestart(seek);
    }

    private void stopTouchToRestart(SeekWrapper.SeekImpl seek) {
        mGLThread.startUp();
        mIMusic.seekToMusic(seek.getProgress());
        mIMusic.startMusic();
        onProgressStart();
    }

    @Override
    public void seekChange(long usedTime) {
        if (mSeekWrapper != null) mSeekWrapper.setProgress((int) usedTime);
        onProgressChange((int) usedTime);
        mIMusic.onProgressChange((int) usedTime);
        recordChange((int) usedTime);
    }

    @Override
    public void actionFinish() {
        finishToTime(mFinishAtTime);
    }

    private void finishToTime(int finishToTime) {
        endRecording();
        mGLThread.stopUp();
        mGLThread.setManual(true);
        mGLThread.setManualUpSeekBar(finishToTime);
        if (mSeekWrapper != null) mSeekWrapper.setProgress(finishToTime);
        mGLThread.setManual(false);
        mIMusic.seekToMusic(finishToTime);
        mIMusic.pauseMusic();
        onProgressChange(finishToTime);
        onProgressStop();
    }

    private void endRecording() {
        if (mIsRecording) {
            mGLThread.setRecording(false);
            mGLRenderWorker.getDrama().videoGroup.endLogoClip = null;
            updateDrama(mGLRenderWorker.getDrama());
            String path = mGLRenderWorker.endRecording();
            scanIntoMediaStore(mContext, path);
            recordFinished(path);
            mIsRecording = false;
        }
    }

    public int getSeekbarMaxValue() {
        return mSeekbarMaxValue;
    }

    public void onPause() {
        pauseVideo();
        mGLThread.onPause();
    }

    public void onResume() {
        mGLThread.onResume();
        mIMusic.onResume(mContext, getUsedTime());
    }

    public void onRestart() {
        mGLThread.onRestart();
        mIMusic.startMusic();
    }

    public void onStop() {
        mGLThread.onStop();
        mIMusic.pauseMusic();
    }

    public void onDestroy() {
        mGLThread.onDestroy();
        mIMusic.onDestroy();
        mGLRenderWorker.onDestroy();
        mGLThread = null;
    }

    public void pauseVideo() {
        mGLThread.stopUp();
        mIMusic.pauseMusic();
        if (isRecording()) mGLRenderWorker.endRecording();
        if (DEBUG) {
            Log.e(TAG, "Thread : "  + Thread.currentThread().getName() + "     " + "endRecording ok");
        }
        onProgressStop();
        if (DEBUG) {
            Log.e(TAG, "Thread : "  + Thread.currentThread().getName() + "     " + "pauseVideo ok");
        }
    }

    public void stopVideo() {
        mGLThread.stopUp();
        mIMusic.stopMusic();
        if (isRecording()) mGLRenderWorker.endRecording();
        onProgressStop();
    }

    public void startVideo() {
        mGLThread.startUp();
        mIMusic.startMusic();
        onProgressStart();
    }

    public boolean isStop() {
        return mGLThread.isStop();
    }

    public void startVideoOnly() {
        mGLThread.startUp();
    }

    public void startVideoWithFinishTime(int finishAtTime) {
        startVideo();
        mFinishAtTime = finishAtTime;
    }

    public void updateVideoTime(int startTime, int endTime) {
        mGLThread.stopUp();
        mGLThread.setManual(true);
        mStartTime = startTime;
        mEndTime = endTime;
        mGLThread.updateTime(mStartTime, mEndTime);
    }

    public void seekToVideo(int time) {
        mGLThread.seekToTime(time);
        mIMusic.seekToMusic(time);
        mIMusic.pauseMusic();
        onProgressStop();
        if (mSeekWrapper != null) mSeekWrapper.setProgress(time);
    }

    private void onProgressInit(int progress, int maxValue) {
        if (mProgressChangeListener != null) {
            mProgressChangeListener.onProgressInit(progress, maxValue);
        }
    }

    private void onProgressStop() {
        if (mProgressChangeListener != null) {
            mProgressChangeListener.onProgressStop();
        }
    }

    private void onProgressChange(int progress) {
        if (mProgressChangeListener != null) {
            mProgressChangeListener.onProgressChange(progress);
        }
    }

    private void onProgressStart() {
        if (mProgressChangeListener != null) {
            mProgressChangeListener.onProgressStart();
        }
    }

    public Drama getDrama() {
        return mGLRenderWorker.getDrama();
    }

    public void updateDrama(Drama drama) {
        updateTime(drama);
        mGLRenderWorker.updateDrama(drama);
        mIMusic.updateDrama(drama);
    }

    public void refreshTransitionRender() {
        mGLRenderWorker.refreshTransitionRender();
    }

    public void refreshSubtitleRender() {
        mGLRenderWorker.refreshSubtitleRender();
    }

    public GLThreadRender getGLThread() {
        return mGLThread;
    }

    public int getUsedTime() {
        return (int) mGLThread.getUsedTime();
    }

    public IMusic getIMusic() {
        return mIMusic;
    }

    public void setStopTouchToRestart(boolean stopTouchToRestart) {
        mIsStopTouchToRestart = stopTouchToRestart;
    }

    public void bitmapLoadReady(String path) {
        isImagePrepared = true;
        if (mBitmapReadyListener != null) mBitmapReadyListener.bitmapReady(path);
        checkSourceReadyToStart();
    }

    public void subtitleLoadReady() {
        isSubtitlePrepared = true;
        checkSourceReadyToStart();
    }

    public void refresh() {
        mGLRenderWorker.updateAll();
    }

    public void requestRender() {
        getGLThread().requestRender();
    }

    private void checkSourceReadyToStart() {
        if (isMoveReadyOk()) {
            requestRender();
        }
    }

    public boolean isMoveReadyOk() {
        return mGLThread != null && mGLThread.isStop()
                && isMusicPrepared
                && isImagePrepared
                && isSubtitlePrepared;
    }

    public int getMaxTime() {
        return mMaxTime;
    }

    public void startRecording() {
        pauseVideo();
        if (!isMoveReadyOk()) return;
        mGLThread.setRecording(true);
        Drama drama = mGLRenderWorker.getDrama();
        EndLogoClip clip = new EndLogoClip();
        clip.startTime = drama.getShowTimeTotal() + 1;
        drama.videoGroup.endLogoClip = clip;
        updateDrama(drama);
        mIsRecording = true;
        recordStart();
        seekToVideo(0);
        mGLRenderWorker.startRecording(FileManager.getInstance().createNewVideoFile());
        startVideoOnly();
    }

    public void setBitmapReadyListener(BitmapReadyListener bitmapReadyListener) {
        this.mBitmapReadyListener = bitmapReadyListener;
    }

    public void recordStart() {
        if (mRecordingListener != null && mIsRecording) {
            mRecordingListener.recordStart();
        }
    }

    public void recordChange(int progress) {
        if (mRecordingListener != null && mIsRecording) {
            mRecordingListener.recordChange(progress);
        }
    }

    public void recordFinished(String path) {
        if (mRecordingListener != null && mIsRecording) {
            mRecordingListener.recordFinished(path);
        }
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    @Override
    public void onProcessorPrepared() {
        isMusicPrepared = true;
        checkSourceReadyToStart();
    }

    @Override
    public void musicPrepareFinished() {
        isMusicPrepared = true;
        checkSourceReadyToStart();
    }

    public TextureLoader getTextureLoader() {
        return mGLRenderWorker.getTextureLoader();
    }

    public interface ProgressChangeListener {
        void onProgressInit(int progress, int maxValue);

        void onProgressStop();

        void onProgressChange(int progress);

        void onProgressStart();
    }

    public interface RecordingListener {
        void recordStart();

        void recordChange(int progress);

        void recordFinished(String path);
    }

    public interface BitmapReadyListener {
        void bitmapReady(String path);
    }
}
