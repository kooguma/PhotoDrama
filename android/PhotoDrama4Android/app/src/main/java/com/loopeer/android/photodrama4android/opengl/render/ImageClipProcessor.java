package com.loopeer.android.photodrama4android.opengl.render;


import android.view.View;

import com.loopeer.android.photodrama4android.opengl.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.opengl.cache.ShaderProgramCache;
import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.model.TransitionClip;
import com.loopeer.android.photodrama4android.opengl.model.VideoGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class ImageClipProcessor {

    private ArrayList<ImageClipDrawer> mImageClipDrawers;
    private ArrayList<TransitionDrawer> mTransitionDrawers;
    private VideoGroup mVideoGroup;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;

    public ImageClipProcessor(MovieMakerGLSurfaceView glSurfaceView) {
        mImageClipDrawers = new ArrayList<>();
        mTransitionDrawers = new ArrayList<>();
        mMovieMakerGLSurfaceView = glSurfaceView;
    }

    public void updateData(VideoGroup videoGroup) {
        ShaderProgramCache.getInstance().init(mMovieMakerGLSurfaceView.getContext());
        setData(videoGroup);
        updateImageClipRenders();
        updateTransitionClipRenders();
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

    public void updateTransitionClipRenders() {
        mTransitionDrawers.clear();
        for (int i = 0; i < mVideoGroup.transitionClips.size(); i++) {
            TransitionClip transitionClip = mVideoGroup.transitionClips.get(i);
            if (transitionClip.showTime == 0) continue;
            try {
                Constructor<TransitionDrawer> constructor = transitionClip.transitionType.getDrawerClass().getConstructor(View.class,
                        TransitionClip.class);
                TransitionDrawer drawer = constructor.newInstance(mMovieMakerGLSurfaceView, transitionClip);
                mTransitionDrawers.add(drawer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        for (ClipDrawer render : mImageClipDrawers) {
            render.drawFrame(usedTime, pMatrix);
        }

        for (ClipDrawer render : mTransitionDrawers) {
            render.drawFrame(usedTime, pMatrix);
        }
    }
}
