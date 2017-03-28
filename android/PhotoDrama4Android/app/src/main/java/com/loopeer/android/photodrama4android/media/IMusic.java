package com.loopeer.android.photodrama4android.media;


public interface IMusic {
    void startMusic();
    void seekToMusic(int progress, int max);
    void pauseMusic();
    void onDestroy();
}
