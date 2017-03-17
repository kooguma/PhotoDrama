package com.loopeer.android.photodrama4android.opengl.render;

import android.content.Context;

import com.loopeer.android.photodrama4android.opengl.IRendererWorker;
import com.loopeer.android.photodrama4android.opengl.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.opengl.model.Drama;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

public class GLRenderWorker implements IRendererWorker {

    private Drama mDrama;
    private Context mContext;
    private ImageClipProcessor mImageClipProcessor;
    private final float[] projectionMatrix = new float[16];
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;

    public GLRenderWorker(Context context, Drama drama, MovieMakerGLSurfaceView textureLoader) {
        mContext = context;
        mMovieMakerGLSurfaceView = textureLoader;
        mDrama = drama;
    }

    public Drama getDrama() {
        return mDrama;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mImageClipProcessor = new ImageClipProcessor(mContext, mMovieMakerGLSurfaceView);
        mImageClipProcessor.updateData(mDrama.videoGroup);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        if (width > height) {
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void drawFrame(Context context, GL10 gl, long usedTime) {
        glClear(GL_COLOR_BUFFER_BIT);
        mImageClipProcessor.drawFrame(usedTime, projectionMatrix);
    }
}
