package com.loopeer.android.photodrama4android.media;


import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class SeekBarImpl implements SeekWrapper.SeekImpl, SeekBar.OnSeekBarChangeListener {
    private SeekBar mSeekBar;
    private List<OnSeekProgressChangeListener> mOnSeekProgressChangeListeners;
    public SeekBarImpl(SeekBar seekBar) {
        mSeekBar = seekBar;
        mOnSeekProgressChangeListeners = new ArrayList<>();
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void setMax(int max) {
        mSeekBar.setMax(max);
    }

    @Override
    public void addOnSeekChangeListener(OnSeekProgressChangeListener listener) {
        mOnSeekProgressChangeListeners.add(listener);
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
        if (mOnSeekProgressChangeListeners == null) return;
        for (OnSeekProgressChangeListener listener :
                mOnSeekProgressChangeListeners) {
            listener.onProgressChanged(this, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mOnSeekProgressChangeListeners == null) return;
        for (OnSeekProgressChangeListener listener :
                mOnSeekProgressChangeListeners) {
            listener.onStartTrackingTouch(this);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mOnSeekProgressChangeListeners == null) return;
        for (OnSeekProgressChangeListener listener :
                mOnSeekProgressChangeListeners) {
            listener.onStopTrackingTouch(this);
        }
    }
}
