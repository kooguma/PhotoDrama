package com.loopeer.android.photodrama4android.media.recorder.gles;

import android.opengl.GLSurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EglHelperLocal {

    public EglHelperLocal(WeakReference<GLSurfaceView> glSurfaceViewWeakRef) {
        mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
    }

    public void makeCurrent() {
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public int swapBuffers() {
        if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
            return mEgl.eglGetError();
        }
        return EGL10.EGL_SUCCESS;
    }

    public int getWidth() {
        return mGLSurfaceViewWeakRef.get().getWidth();
    }

    public int getHeight() {
        return mGLSurfaceViewWeakRef.get().getHeight();
    }

    private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;
    public EGL10 mEgl;
    EGLDisplay mEglDisplay;
    EGLSurface mEglSurface;
    EGLConfig mEglConfig;
    public EGLContext mEglContext;

    public void update(EGL10 egl, EGLContext renderContext, EGLDisplay display, EGLConfig eglConfig) {
        mEgl = egl;
        mEglDisplay = display;
        mEglContext = renderContext;
        mEglConfig = eglConfig;
    }

    public void update(EGLSurface result) {
        mEglSurface = result;
    }
}