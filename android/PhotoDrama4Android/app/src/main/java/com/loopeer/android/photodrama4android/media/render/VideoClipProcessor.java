package com.loopeer.android.photodrama4android.media.render;


import android.view.TextureView;
import android.view.View;

import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.MovieMakerTextureView;
import com.loopeer.android.photodrama4android.media.SubtitleTextureLoader;
import com.loopeer.android.photodrama4android.media.TextureLoader;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.cache.ShaderProgramCache;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.model.SubtitleClip;
import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.model.VideoGroup;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class VideoClipProcessor {

    private ArrayList<ImageClipDrawer> mImageClipDrawers;
    private ArrayList<TransitionDrawer> mTransitionDrawers;
    private ArrayList<SubtitleClipDrawer> mSubtitleClipDrawers;
    private EndLogoClipDrawer mEndLogoClipDrawer;
    private VideoGroup mVideoGroup;
    private MovieMakerTextureView mTextureView;


    private TextureLoader mTextureLoader;
    private SubtitleTextureLoader mTextTextureLoader;

    public VideoClipProcessor(TextureView textureView) {
        mImageClipDrawers = new ArrayList<>();
        mTransitionDrawers = new ArrayList<>();
        mSubtitleClipDrawers = new ArrayList<>();
        mTextureView = (MovieMakerTextureView)textureView;
        ShaderProgramCache.getInstance().init(mTextureView.getContext());
    }

    public synchronized void updateData(VideoGroup videoGroup) {
        setData(videoGroup);
        notifyData();
    }

    public void notifyData() {
        updateImageClipRenders();
        updateTransitionClipRenders();
        updateSubtitleClipRenders();
        updateEndLogoClipRenders();
    }

    private void setData(VideoGroup videoGroup) {
        mVideoGroup = videoGroup;
    }

    private void updateImageClipRenders() {
        mImageClipDrawers.clear();
        for (int i = 0; i < mVideoGroup.imageClips.size(); i++) {
            ImageClip imageClip = mVideoGroup.imageClips.get(i);
            ImageClipDrawer imageClipRender = new ImageClipDrawer(mTextureView, imageClip);
            if (mTextureLoader != null) imageClipRender.preLoadTexture(mTextureView, mTextureLoader);
            mImageClipDrawers.add(imageClipRender);
        }
    }

    public void updateSubtitleClipRenders() {
        mSubtitleClipDrawers.clear();
        for (int i = 0; i < mVideoGroup.subtitleClips.size(); i++) {
            SubtitleClip subtitleClip = mVideoGroup.subtitleClips.get(i);
            SubtitleClipDrawer subtitleClipDrawer = new SubtitleClipDrawer(mTextureView, subtitleClip);
            if (mTextTextureLoader != null) subtitleClipDrawer.preLoadTexture(mTextureView, mTextTextureLoader);
            mSubtitleClipDrawers.add(subtitleClipDrawer);
        }
        if (mVideoGroup.subtitleClips.isEmpty()) {
            VideoPlayManagerContainer.getDefault().subtitleLoadReady(mTextureView.getContext());
        }
    }

    public void updateTransitionClipRenders() {
        mTransitionDrawers.clear();
        for (int i = 0; i < mVideoGroup.transitionClips.size(); i++) {
            TransitionClip transitionClip = mVideoGroup.transitionClips.get(i);
            if (transitionClip.showTime == 0) continue;
            try {
                Constructor<TransitionDrawer> constructor = transitionClip.transitionType.getDrawerClass()
                        .getConstructor(MovieMakerGLSurfaceView.class,
                        TransitionClip.class);
                TransitionDrawer drawer = constructor.newInstance(mTextureView, transitionClip);
                mTransitionDrawers.add(drawer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEndLogoClipRenders() {
        if (mVideoGroup.endLogoClip != null) {
            mEndLogoClipDrawer = new EndLogoClipDrawer(mTextureView, mVideoGroup.endLogoClip);
        } else {
            mEndLogoClipDrawer = null;
        }
    }

    public synchronized void drawFrame(long usedTime, float[] pMatrix) {
        for (ClipDrawer render : mImageClipDrawers) {
            render.drawFrame(usedTime, pMatrix);
        }

        for (ClipDrawer render : mTransitionDrawers) {
            render.drawFrame(usedTime, pMatrix);
        }

        for (ClipDrawer render : mSubtitleClipDrawers) {
            render.drawFrame(usedTime, pMatrix);
        }

        if (mEndLogoClipDrawer != null) {
            mEndLogoClipDrawer.drawFrame(usedTime, pMatrix);
        }
    }

    public void updateSurfaceAndSubtitle(WindowSurface windowSurface, EglCore eglCore) {
        /*mWindowSurface = windowSurface;
        mEglCore = eglCore;*/

        mTextureLoader = new TextureLoader(mTextureView.getContext());
        mTextureLoader.update(windowSurface, eglCore);
        if (!mTextureLoader.isAlive()) mTextureLoader.start();

        mTextTextureLoader = new SubtitleTextureLoader(mTextureView.getContext());
        mTextTextureLoader.update(windowSurface, eglCore);
        if (!mTextTextureLoader.isAlive()) mTextTextureLoader.start();
    }
}
