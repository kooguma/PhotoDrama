package com.loopeer.android.photodrama4android.event;

import com.loopeer.android.photodrama4android.model.Voice;

public class MusicDownLoadSuccessEvent {

    public Voice voice;

    public MusicDownLoadSuccessEvent(Voice voice) {
        this.voice = voice;
    }
}
