package com.loopeer.android.photodrama4android.opengl.render;


import android.view.View;

import com.loopeer.android.photodrama4android.opengl.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.model.TransitionClip;
import com.loopeer.android.photodrama4android.opengl.model.VideoGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class ImageClipProcessor {

    private ArrayList<ClipDrawer> mClipDrawers;
    private VideoGroup mVideoGroup;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;

    public ImageClipProcessor(MovieMakerGLSurfaceView glSurfaceView) {
        mClipDrawers = new ArrayList<>();
        mMovieMakerGLSurfaceView = glSurfaceView;
    }

    public void updateData(VideoGroup videoGroup) {
        setData(videoGroup);
        updateImageClipRenders();
    }

    private void setData(VideoGroup videoGroup) {
        mVideoGroup = videoGroup;
    }

    private void updateImageClipRenders() {
        for (int i = 0; i < mVideoGroup.imageClips.size(); i++) {
            ImageClip imageClip = mVideoGroup.imageClips.get(i);
            ImageClipDrawer imageClipRender = new ImageClipDrawer(mMovieMakerGLSurfaceView, imageClip);
            imageClipRender.preLoadTexture(mMovieMakerGLSurfaceView);
            mClipDrawers.add(imageClipRender);
        }

        for (int i = 0; i < mVideoGroup.transitionClips.size(); i++) {
            TransitionClip transitionClip = mVideoGroup.transitionClips.get(i);
            if (transitionClip.showTime == 0) continue;
            try {
                Constructor<TransitionDrawer> constructor = transitionClip.transitionType.getDrawerClass().getConstructor(View.class,
                        TransitionClip.class);
                TransitionDrawer drawer = constructor.newInstance(mMovieMakerGLSurfaceView, transitionClip);
                mClipDrawers.add(drawer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        for (ClipDrawer render : mClipDrawers) {
            if (!(render instanceof TransitionDrawer))
                render.drawFrame(usedTime, pMatrix);
        }

        for (ClipDrawer render : mClipDrawers) {
            if (render instanceof TransitionDrawer)
                render.drawFrame(usedTime, pMatrix);
        }
    }
}
