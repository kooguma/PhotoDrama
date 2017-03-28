package com.loopeer.android.photodrama4android.media;


import android.content.Context;

import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.render.GLRenderWorker;
import com.loopeer.android.photodrama4android.media.render.GLThreadRender;

public class VideoPlayerManager implements OnSeekProgressChangeListener, IUpSeekBar, IPlayerLife {

    private SeekWrapper mSeekWrapper;
    private GLThreadRender mGLThread;
    private ProgressChangeListener mProgressChangeListener;
    private GLRenderWorker mGLRenderWorker;
    private int mSeekbarMaxValue;
    private IMusic mIMusic;
    private Context mContext;
    private int mStartTime;
    private int mEndTime;
    private int mFinishAtTime;
    private boolean mIsStopTouchToRestart;

    public VideoPlayerManager(SeekWrapper seekWrapper, MovieMakerGLSurfaceView glSurfaceView, Drama drama) {
        mContext = glSurfaceView.getContext();
        mSeekWrapper = seekWrapper;
        mGLRenderWorker = new GLRenderWorker(mContext, drama, glSurfaceView);
        mGLThread = new GLThreadRender(glSurfaceView.getContext(), glSurfaceView, mGLRenderWorker);
        mIMusic = new MusicManager(mContext);

        updateTime(drama);
        init();
    }

    private void updateTime(Drama drama) {
        int totalTime = drama.getShowTimeTotal();
        setSeekBarMaxValue(totalTime);
        if (mSeekWrapper != null) mSeekWrapper.setMax(totalTime);
        mStartTime = 0;
        mFinishAtTime = mStartTime;
        mEndTime = totalTime;
        mGLThread.updateTime(mStartTime, mEndTime);

        onProgressInit(mStartTime, mSeekbarMaxValue);
    }

    private void init() {
        if (mSeekWrapper != null) mSeekWrapper.setOnSeekChangeListener(this);
        mGLThread.setUpSeekBarListener(this);
    }

    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        mProgressChangeListener = progressChangeListener;
        onProgressInit(mStartTime, mSeekbarMaxValue);
    }

    public void setSeekBarMaxValue(int seekBarMaxValue) {
        mSeekbarMaxValue = seekBarMaxValue;
    }

    @Override
    public void onProgressChanged(SeekWrapper.SeekImpl seek, int progress, boolean fromUser) {
        mGLThread.setManualUpSeekBar(progress);
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
        mIMusic.seekToMusic(seek.getProgress(), mSeekbarMaxValue);
        mIMusic.startMusic();
        onProgressStart(seek.getProgress(), mSeekbarMaxValue);
    }

    @Override
    public void upSeekBar(long usedTime) {
        if (mSeekWrapper != null) mSeekWrapper.setProgress((int) usedTime);
    }

    @Override
    public void actionFinish() {
        finishToTime(mFinishAtTime);
    }

    private void finishToTime(int finishToTime) {
        mGLThread.stopUp();
        mGLThread.setManual(true);
        mGLThread.setManualUpSeekBar(finishToTime);
        if (mSeekWrapper != null) mSeekWrapper.setProgress(finishToTime);
        mGLThread.setManual(false);
        mIMusic.seekToMusic(finishToTime, mSeekbarMaxValue);
        mIMusic.pauseMusic();
        onProgressChange(finishToTime);
        onProgressStop();
    }

    public int getSeekbarMaxValue() {
        return mSeekbarMaxValue;
    }

    public void onPause() {
        mGLThread.onPause();
    }

    public void onResume() {
        mGLThread.onResume();
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
        mGLThread = null;
    }

    public void pauseVideo() {
        mGLThread.stopUp();
        mIMusic.pauseMusic();
        onProgressStop();
    }

    public void startVideo() {
        mGLThread.startUp();
        mIMusic.startMusic();
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
        if (mSeekWrapper != null) mSeekWrapper.setProgress(time);
    }

    public void refreshIfWait() {
        if (mGLThread.isStop())
            mGLThread.seekToTime(mGLThread.getUsedTime());
    }

    private void onProgressInit(int progress, int maxValue) {
        if (mProgressChangeListener != null)
            mProgressChangeListener.onProgressInit(progress, maxValue);
    }

    private void onProgressStop() {
        if (mProgressChangeListener != null)
            mProgressChangeListener.onProgressStop();
    }

    private void onProgressChange(int progress) {
        if (mProgressChangeListener != null)
            mProgressChangeListener.onProgressChange(progress);
    }

    private void onProgressStart(int progress, int maxValue) {
        if (mProgressChangeListener != null)
            mProgressChangeListener.onProgressStart(progress, maxValue);
    }

    public Drama getDrama() {
        return mGLRenderWorker.getDrama();
    }

    public void updateDrama(Drama drama) {
        updateTime(drama);
        mGLRenderWorker.updateDrama(drama);
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

    public void setStopTouchToRestart(boolean stopTouchToRestart) {
        mIsStopTouchToRestart = stopTouchToRestart;
    }

    public interface ProgressChangeListener {
        void onProgressInit(int progress, int maxValue);

        void onProgressStop();

        void onProgressChange(int progress);

        void onProgressStart(int progress, int maxValue);
    }
}
