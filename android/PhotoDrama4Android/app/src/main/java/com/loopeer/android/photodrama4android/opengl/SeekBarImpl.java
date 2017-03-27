package com.loopeer.android.photodrama4android.opengl;


import android.widget.SeekBar;

public class SeekBarImpl implements SeekWrapper.SeekImpl, SeekBar.OnSeekBarChangeListener {
    private SeekBar mSeekBar;
    private OnSeekProgressChangeListener mOnSeekProgressChangeListener;
    public SeekBarImpl(SeekBar seekBar) {
        mSeekBar = seekBar;
    }

    @Override
    public void setMax(int max) {
        mSeekBar.setMax(max);
    }

    @Override
    public void setOnSeekChangeListener(OnSeekProgressChangeListener listener) {
        mOnSeekProgressChangeListener = listener;
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void setProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    @Override
    public int getProgress() {
        return mSeekBar.getProgress();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mOnSeekProgressChangeListener.onProgressChanged(this, progress, fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mOnSeekProgressChangeListener.onStartTrackingTouch(this);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mOnSeekProgressChangeListener.onStopTrackingTouch(this);
    }
}
