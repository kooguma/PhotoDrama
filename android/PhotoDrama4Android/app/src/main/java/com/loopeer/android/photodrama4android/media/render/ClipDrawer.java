package com.loopeer.android.photodrama4android.media.render;


import android.opengl.GLSurfaceView;
import android.view.TextureView;
import android.view.View;

import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.MovieMakerTextureView;
import com.loopeer.android.photodrama4android.media.data.VertexArray;

public abstract class ClipDrawer {
    protected static VertexArray vertexArray;
    protected int mViewWidth;
    protected int mViewHeight;
    public static float[] mVertexData;
    protected MovieMakerTextureView mGLSurfaceView;

    static {
        initVertex();
    }

    public ClipDrawer(TextureView view) {
        mGLSurfaceView = (MovieMakerTextureView) view;
        mViewWidth = view.getWidth();
        mViewHeight = view.getHeight();
    }

    private static void initVertex() {
        mVertexData = new float[]{
                -1f, 1f, 0f, 0f,
                -1f, -1f, 0f, 1f,
                1f, -1f, 1f, 1f,
                1f, -1f, 1f, 1f,
                1f, 1f, 1f, 0f,
                -1f, 1f, 0f, 0f
        };
        vertexArray = new VertexArray(mVertexData);
    }

    public abstract void drawFrame(long usedTime, float[] pMatrix);

    protected VertexArray getVertexArray() {
        return vertexArray;
    }
}
