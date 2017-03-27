package com.loopeer.android.photodrama4android.opengl;


import android.content.Context;
import android.widget.SeekBar;

import com.loopeer.android.photodrama4android.opengl.model.Drama;
import com.loopeer.android.photodrama4android.opengl.render.GLRenderWorker;
import com.loopeer.android.photodrama4android.opengl.render.GLThreadRender;

public class VideoPlayerManager implements SeekBar.OnSeekBarChangeListener, IUpSeekBar, IPlayerLife {

    private SeekBar mSeekBar;
    private GLThreadRender mGLThread;
    private ProgressChangeListener mProgressChangeListener;
    private GLRenderWorker mGLRenderWorker;
    private int mSeekbarMaxValue;
    private IMusic mIMusic;
    private Context mContext;
    private int mStartTime;
    private int mEndTime;
    private int mFinishAtTime;

    public VideoPlayerManager(SeekBar seekBar, MovieMakerGLSurfaceView glSurfaceView, Drama drama) {
        mContext = glSurfaceView.getContext();
        mSeekBar = seekBar;
        mGLRenderWorker = new GLRenderWorker(mContext, drama, glSurfaceView);
        mGLThread = new GLThreadRender(glSurfaceView.getContext(), glSurfaceView, mGLRenderWorker);
        mIMusic = new MusicManager();

        updateTime(drama);
        init();
    }

    private void updateTime(Drama drama) {
        int totalTime = drama.getShowTimeTotal();
        setSeekBarMaxValue(totalTime);
        if (mSeekBar != null) mSeekBar.setMax(totalTime);
        mStartTime = 0;
        mFinishAtTime = mStartTime;
        mEndTime = totalTime;
        mGLThread.updateTime(mStartTime, mEndTime);

        onProgressInit(mStartTime, mSeekbarMaxValue);
    }

    private void init() {
        if (mSeekBar != null) mSeekBar.setOnSeekBarChangeListener(this);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mGLThread.setManualUpSeekBar(progress);
        mProgressChangeListener.onProgressChange(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mGLThread.stopUp();
        mGLThread.setManual(true);
        mIMusic.pauseMusic();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mGLThread.setManual(false);
        mGLThread.setUsedTime(seekBar.getProgress());
        mGLThread.startUp();
        mIMusic.seekToMusic(seekBar.getProgress(), mSeekbarMaxValue);
        mIMusic.startMusic();
        onProgressStart(seekBar.getProgress(), mSeekbarMaxValue);
    }

    @Override
    public void upSeekBar(long usedTime) {
        if (mSeekBar != null) mSeekBar.setProgress((int) usedTime);
    }

    @Override
    public void actionFinish() {
        finishToTime(mFinishAtTime);
    }

    private void finishToTime(int finishToTime) {
        mGLThread.stopUp();
        mGLThread.setManual(true);
        mGLThread.setManualUpSeekBar(finishToTime);
        if (mSeekBar != null) mSeekBar.setProgress(finishToTime);
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
        if (mSeekBar != null) mSeekBar.setProgress(time);
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

    public GLThreadRender getGLThread() {
        return mGLThread;
    }

    public interface ProgressChangeListener {
        void onProgressInit(int progress, int maxValue);

        void onProgressStop();

        void onProgressChange(int progress);

        void onProgressStart(int progress, int maxValue);
    }
}
