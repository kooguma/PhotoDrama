package com.loopeer.android.photodrama4android.event;

import com.loopeer.android.photodrama4android.model.Voice;

public class MusicDownProgressEvent {

    public Voice voice;
    public long progress;

    public MusicDownProgressEvent(Voice voice, long progress) {
        this.voice = voice;
        this.progress = progress;
    }
}
