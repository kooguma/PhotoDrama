package com.loopeer.android.photodrama4android.opengl;


import android.widget.SeekBar;

public class SeekBarImpl implements SeekWrapper.SeekImpl {
    private SeekBar mSeekBar;

    public SeekBarImpl(SeekBar seekBar) {
        mSeekBar = seekBar;
    }

    @Override
    public void setMax(int max) {
        mSeekBar.setMax(max);
    }

    @Override
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        mSeekBar.setOnSeekBarChangeListener(listener);
    }

    @Override
    public void setProgress(int progress) {
        mSeekBar.setProgress(progress);
    }
}
