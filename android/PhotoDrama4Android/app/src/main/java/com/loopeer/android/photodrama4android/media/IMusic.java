package com.loopeer.android.photodrama4android.media;

import com.loopeer.android.photodrama4android.media.model.Drama;

public interface IMusic {
    void updateDrama(Drama drama);
    void startMusic();
    void seekToMusic(int progress);
    void pauseMusic();
    void onProgressChange(int progress);
    void onPause();
    void onDestroy();
}
