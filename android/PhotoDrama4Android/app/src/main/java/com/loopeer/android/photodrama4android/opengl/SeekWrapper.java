package com.loopeer.android.photodrama4android.opengl;


import android.widget.SeekBar;

public class SeekWrapper {

    interface SeekImpl {
        void setMax(int max);

        void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener);

        void setProgress(int progress);
    }

    private SeekImpl mSeekImpl;

    public SeekWrapper(SeekBar seekBar) {
        mSeekImpl = new SeekBarImpl(seekBar);
    }

    public void setMax(int max) {
        mSeekImpl.setMax(max);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        mSeekImpl.setOnSeekBarChangeListener(listener);
    }

    public void setProgress(int progress) {
        mSeekImpl.setProgress(progress);
    }
}
