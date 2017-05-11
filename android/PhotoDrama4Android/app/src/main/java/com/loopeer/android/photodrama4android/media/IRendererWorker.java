package com.loopeer.android.photodrama4android.media;

import android.content.Context;

import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

public interface IRendererWorker {

    void onSurfaceCreated(WindowSurface windowSurface, EglCore eglCore);

    void onSurfaceChanged(WindowSurface windowSurface, int width, int height);

    void drawFrame(Context context, WindowSurface windowSurface, long usedTime);

    void onSurfaceDestroy();

}