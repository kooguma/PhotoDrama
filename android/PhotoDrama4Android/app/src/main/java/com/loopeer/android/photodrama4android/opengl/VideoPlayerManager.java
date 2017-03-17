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

    public VideoPlayerManager(SeekBar seekBar, MovieMakerGLSurfaceView glSurfaceView, Drama drama) {
        mContext = glSurfaceView.getContext();
        mSeekBar = seekBar;
        int totalTime = drama.getShowTimeTotal();
        setSeekBarMaxValue(totalTime);
        if (mSeekBar != null) mSeekBar.setMax(totalTime);
        mGLRenderWorker = new GLRenderWorker(mContext, drama, glSurfaceView);
        mGLThread = new GLThreadRender(glSurfaceView.getContext(), glSurfaceView, mGLRenderWorker);
        mStartTime = 0;
        mEndTime = totalTime;
        mGLThread.updateTime(mStartTime, mEndTime);
        mIMusic = new MusicManager();
        init();
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
        mGLThread.stopUp();
        mGLThread.setManual(true);
        mGLThread.setManualUpSeekBar(mStartTime);
        if (mSeekBar != null) mSeekBar.setProgress(mStartTime);
        mGLThread.setManual(false);
        mIMusic.seekToMusic(mStartTime, mSeekbarMaxValue);
        mIMusic.pauseMusic();
        onProgressChange(mStartTime);
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

    public void updateVideoTime(int startTime, int endTime) {
        mStartTime = startTime;
        mEndTime = endTime;
        mGLThread.updateTime(mStartTime, mEndTime);
    }

    public void seekToVideo(int time) {
        mGLThread.seekToTime(time);
    }

    private void onProgressInit(int progress, int maxValue) {
        if (mProgressChangeListener != null) mProgressChangeListener.onProgressInit(progress, maxValue);
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

    public interface ProgressChangeListener {
        void onProgressInit(int progress, int maxValue);

        void onProgressStop();

        void onProgressChange(int progress);

        void onProgressStart(int progress, int maxValue);
    }
}
