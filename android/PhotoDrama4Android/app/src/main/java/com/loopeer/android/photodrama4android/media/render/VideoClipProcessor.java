package com.loopeer.android.photodrama4android.media.render;


import android.view.View;

import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.cache.ShaderProgramCache;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.model.SubtitleClip;
import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.model.VideoGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class VideoClipProcessor {

    private ArrayList<ImageClipDrawer> mImageClipDrawers;
    private ArrayList<TransitionDrawer> mTransitionDrawers;
    private ArrayList<SubtitleClipDrawer> mSubtitleClipDrawers;
    private EndLogoClipDrawer mEndLogoClipDrawer;
    private VideoGroup mVideoGroup;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;

    public VideoClipProcessor(MovieMakerGLSurfaceView glSurfaceView) {
        mImageClipDrawers = new ArrayList<>();
        mTransitionDrawers = new ArrayList<>();
        mSubtitleClipDrawers = new ArrayList<>();
        mMovieMakerGLSurfaceView = glSurfaceView;
        ShaderProgramCache.getInstance().init(mMovieMakerGLSurfaceView.getContext());
    }

    public synchronized void updateData(VideoGroup videoGroup) {
        setData(videoGroup);
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
            ImageClipDrawer imageClipRender = new ImageClipDrawer(mMovieMakerGLSurfaceView, imageClip);
            imageClipRender.preLoadTexture(mMovieMakerGLSurfaceView);
            mImageClipDrawers.add(imageClipRender);
        }
    }

    public void updateSubtitleClipRenders() {
        mSubtitleClipDrawers.clear();
        for (int i = 0; i < mVideoGroup.subtitleClips.size(); i++) {
            SubtitleClip subtitleClip = mVideoGroup.subtitleClips.get(i);
            SubtitleClipDrawer subtitleClipDrawer = new SubtitleClipDrawer(mMovieMakerGLSurfaceView, subtitleClip);
            subtitleClipDrawer.preLoadTexture(mMovieMakerGLSurfaceView);
            mSubtitleClipDrawers.add(subtitleClipDrawer);
        }
        if (mVideoGroup.subtitleClips.isEmpty()) {
            VideoPlayManagerContainer.getDefault().subtitleLoadReady(mMovieMakerGLSurfaceView.getContext());
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
                TransitionDrawer drawer = constructor.newInstance(mMovieMakerGLSurfaceView, transitionClip);
                mTransitionDrawers.add(drawer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEndLogoClipRenders() {
        if (mVideoGroup.endLogoClip != null) {
            mEndLogoClipDrawer = new EndLogoClipDrawer(mMovieMakerGLSurfaceView, mVideoGroup.endLogoClip);
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
}
