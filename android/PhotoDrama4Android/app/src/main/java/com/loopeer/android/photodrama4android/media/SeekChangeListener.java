package com.loopeer.android.photodrama4android.media;

public interface SeekChangeListener {
   void seekChange(long usedTime);
   void actualFinishAt(long usedTime);
   void actionFinish();
}
