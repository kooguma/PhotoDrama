package com.loopeer.android.photodrama4android.media;


import android.widget.SeekBar;

import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;

public class SeekWrapper {

    public interface SeekImpl {
        void setMax(int max);

        void setOnSeekChangeListener(OnSeekProgressChangeListener listener);

        void setProgress(int progress);

        int getProgress();
    }

    private SeekImpl mSeekImpl;

    public SeekWrapper(SeekBar seekBar) {
        mSeekImpl = new SeekBarImpl(seekBar);
    }

    public SeekWrapper(ScrollSelectView seekBar) {
        mSeekImpl = new ScrollSelectViewSeekImpl(seekBar);
    }

    public void setMax(int max) {
        mSeekImpl.setMax(max);
    }

    public void setOnSeekChangeListener(OnSeekProgressChangeListener listener) {
        mSeekImpl.setOnSeekChangeListener(listener);
    }

    public void setProgress(int progress) {
        mSeekImpl.setProgress(progress);
    }
}
