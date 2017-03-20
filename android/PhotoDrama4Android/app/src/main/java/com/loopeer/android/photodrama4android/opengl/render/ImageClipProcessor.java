package com.loopeer.android.photodrama4android.opengl.render;


import android.content.Context;

import com.loopeer.android.photodrama4android.opengl.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.model.ScaleTranslateRatio;
import com.loopeer.android.photodrama4android.opengl.model.VideoGroup;

import java.util.ArrayList;

public class ImageClipProcessor {

    private ArrayList<ImageClipDrawer> mImageClipRenders;
    private ArrayList<ImageClip> mImageClips;
    private Context mContext;
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;

    public ImageClipProcessor(Context context, MovieMakerGLSurfaceView textureLoader) {
        mContext = context;
        mImageClips = new ArrayList<>();
        mImageClipRenders = new ArrayList<>();
        mMovieMakerGLSurfaceView = textureLoader;

        updateImageClipRenders();
    }

    public void updateData(VideoGroup videoGroup) {
        mImageClips.clear();
        mImageClips.addAll(videoGroup.imageClips);
        updateImageClipRenders();
    }

    private void updateImageClipRenders() {
        ScaleTranslateRatio large = new ScaleTranslateRatio(3f, 0f, 0f);
        ScaleTranslateRatio deft = new ScaleTranslateRatio(2.5f, 0f, 0f);
        for (int i = 0; i < mImageClips.size(); i++) {
            ImageClip imageClip = mImageClips.get(i);
            if (i % 2 == 0) {
                imageClip.startScaleTransRatio = large;
                imageClip.endScaleTransRatio = deft;
            } else {
                imageClip.startScaleTransRatio = deft;
                imageClip.endScaleTransRatio = large;
            }
            ImageClipDrawer imageClipRender = new ImageClipDrawer(mContext, imageClip);
            imageClipRender.preLoadTexture(mMovieMakerGLSurfaceView);

            mImageClipRenders.add(imageClipRender);
        }
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        for (ImageClipDrawer render : mImageClipRenders) {
            render.drawFrame(usedTime, pMatrix);
        }
    }
}
