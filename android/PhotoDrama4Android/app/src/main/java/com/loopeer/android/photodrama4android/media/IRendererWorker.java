package com.loopeer.android.photodrama4android.media;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public interface IRendererWorker {

    void onSurfaceCreated(GL10 gl, EGLConfig config);

    void onSurfaceChanged(GL10 gl, int width, int height);

    void drawFrame(Context context, GL10 gl, long usedTime);
}