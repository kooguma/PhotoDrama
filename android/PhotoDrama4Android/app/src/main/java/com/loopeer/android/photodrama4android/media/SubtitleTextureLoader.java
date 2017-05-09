package com.loopeer.android.photodrama4android.media;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLSurface;
import android.os.Bundle;
import android.os.Message;
import com.loopeer.android.photodrama4android.media.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;
import com.loopeer.android.photodrama4android.media.utils.TextureHelper;
import java.util.ArrayList;
import java.util.List;

public class SubtitleTextureLoader extends Thread {

    private EglCore mEglCore;
    private Context mContext;
    private boolean mIsFinish;

    public List<HandlerWrapper> mHandlerWrappers = new ArrayList<>();

    public SubtitleTextureLoader() {
    }

    public SubtitleTextureLoader(Context context) {
        mContext = context;
    }

    public void run() {
        EGLContext textureContext = EGL14.eglCreateContext(mEglCore.mEGLDisplay, mEglCore.mEGLConfig, mEglCore.mEGLContext, mEglCore.getAttribList(), 0);

        int pbufferAttribs[] = {EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_TEXTURE_TARGET,
                EGL14.EGL_NO_TEXTURE, EGL14.EGL_TEXTURE_FORMAT, EGL14.EGL_NO_TEXTURE,
                EGL14.EGL_NONE};

        EGLSurface localSurface = EGL14.eglCreatePbufferSurface(mEglCore.mEGLDisplay, mEglCore.mEGLConfig, pbufferAttribs, 0);

        if (!EGL14.eglMakeCurrent(mEglCore.mEGLDisplay, localSurface, localSurface, textureContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
        while (!mIsFinish) {
            synchronized (this) {
                try {
                    if (mHandlerWrappers.isEmpty())
                        this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handleSubtitleTexture();
        }
    }

    private void handleSubtitleTexture() {
        if (mHandlerWrappers.isEmpty()) return;
        HandlerWrapper<SubtitleInfo, SubtitleInfo> handlerWrapper = mHandlerWrappers.get(0);
        if (handlerWrapper != null && handlerWrapper.getType() == HandlerWrapper.TYPE_LOAD_SUBTITLE) {
            SubtitleInfo subtitleInfo = TextureHelper.loadTexture(mContext, handlerWrapper.getData());
            returnSubtitleInfo(handlerWrapper, subtitleInfo);
            mHandlerWrappers.remove(handlerWrapper);
        }
    }


    private void returnSubtitleInfo(HandlerWrapper<SubtitleInfo, SubtitleInfo> handlerWrapper, SubtitleInfo subtitleInfo) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putSerializable(handlerWrapper.getKey(), subtitleInfo);
        msg.setData(b);
        handlerWrapper.sendMessage(msg);
    }

    public synchronized void loadSubtitleTexture(HandlerWrapper<SubtitleInfo, SubtitleInfo> handlerWrapper) {
        mHandlerWrappers.add(handlerWrapper);
        if (isAlive())
            notify();
    }

    public void finish() {
        mIsFinish = true;
    }

    public void update(WindowSurface windowSurface, EglCore eglCore) {
        mEglCore = eglCore;
    }
}