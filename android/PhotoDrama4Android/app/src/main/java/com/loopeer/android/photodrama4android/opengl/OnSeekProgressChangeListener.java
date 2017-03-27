package com.loopeer.android.photodrama4android.opengl;

public interface OnSeekProgressChangeListener {

        void onProgressChanged(SeekWrapper.SeekImpl seek, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekWrapper.SeekImpl seek);

        void onStopTrackingTouch(SeekWrapper.SeekImpl seek);
}