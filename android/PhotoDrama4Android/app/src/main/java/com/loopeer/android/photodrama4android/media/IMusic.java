package com.loopeer.android.photodrama4android.media;

import android.content.Context;

import com.loopeer.android.photodrama4android.media.model.Drama;

public interface IMusic {
    void updateDrama(Drama drama);
    void startMusic();
    void seekToMusic(int progress);
    void pauseMusic();
    void stopMusic();
    void onProgressChange(int progress);
    void onResume(Context context, int progress);
    void onPause();
    void onDestroy();
}
