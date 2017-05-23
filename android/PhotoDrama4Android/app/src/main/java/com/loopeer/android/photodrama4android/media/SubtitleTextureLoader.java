package com.loopeer.android.photodrama4android.media;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLSurface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.loopeer.android.photodrama4android.media.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;
import com.loopeer.android.photodrama4android.media.utils.TextureHelper;
import java.util.ArrayList;
import java.util.List;

import static com.loopeer.android.photodrama4android.media.recorder.gles.EglCore.checkEglError;

public class SubtitleTextureLoader extends Thread {

    private static final String TAG = "SubtitleTextureLoader";

    private EglCore mEglCore;
    private WindowSurface mWindowSurface;
    private Context mContext;
    private boolean mIsFinish;
    private EGLContext mEGLContext;

    public List<HandlerWrapper> mHandlerWrappers = new ArrayList<>();

    public SubtitleTextureLoader() {
    }

    public SubtitleTextureLoader(Context context) {
        mContext = context;
    }

    public void run() {
        boolean isContextValidate;
        try {
            EglCore eglCore = new EglCore(mEglCore.mEGLContext, EglCore.FLAG_TRY_GLES3);
            EGLSurface eglSurface = eglCore.createOffscreenSurface(1, 1);
            eglCore.makeCurrent(eglSurface);
            isContextValidate = true;
        } catch (RuntimeException e) {
            isContextValidate = false;
        }
        while (!mIsFinish) {
            synchronized (this) {
                try {
                    if (mHandlerWrappers.isEmpty() || !isContextValidate)
                        this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (isContextValidate) handleSubtitleTexture();
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
        mWindowSurface = windowSurface;
        mEGLContext = EGL14.eglCreateContext(mEglCore.mEGLDisplay, mEglCore.mEGLConfig, mEglCore.mEGLContext, mEglCore.getAttribList(), 0);

    }
}