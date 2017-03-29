package com.loopeer.android.photodrama4android.media;


public interface IMusic {
    void startMusic();
    void seekToMusic(int progress);
    void pauseMusic();
    void onProgressChange(int progress);
    void onPause();
    void onDestroy();
}
