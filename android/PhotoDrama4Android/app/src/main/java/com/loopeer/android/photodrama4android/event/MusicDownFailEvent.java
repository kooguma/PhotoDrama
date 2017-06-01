package com.loopeer.android.photodrama4android.event;

import com.loopeer.android.photodrama4android.model.Voice;

public class MusicDownFailEvent {

    public Voice voice;
    public String failMessage;

    public MusicDownFailEvent(Voice voice, String failMessage) {
        this.voice = voice;
        this.failMessage = failMessage;
    }
}
