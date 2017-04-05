package com.loopeer.android.photodrama4android.media.render;


import android.view.View;

import com.loopeer.android.photodrama4android.media.data.VertexArray;

public abstract class ClipDrawer {
    protected static VertexArray vertexArray;
    protected int mViewWidth;
    protected int mViewHeight;
    public static float[] mVertexData;

    static {
        initVertex();
    }

    public ClipDrawer(View view) {
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
