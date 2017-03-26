package com.loopeer.android.photodrama4android.opengl.render;


import android.view.View;

import com.loopeer.android.photodrama4android.opengl.data.VertexArray;

public abstract class ClipDrawer {
    protected VertexArray vertexArray;

    protected int mViewWidth;
    protected int mViewHeight;

    public ClipDrawer(View view) {
        mViewWidth = view.getWidth();
        mViewHeight = view.getHeight();
    }

    public abstract void drawFrame(long usedTime, float[] pMatrix);

    public void createVertex() {
        vertexArray = new VertexArray(createData());
    }

    public float[] createData() {
        float[] result = new float[]{
                -1f, 1f, 0f, 0f,
                -1f, -1f, 0f, 1f,
                1f, -1f, 1f, 1f,
                1f, -1f, 1f, 1f,
                1f, 1f, 1f, 0f,
                -1f, 1f, 0f, 0f
        };
        return result;
    }
}
