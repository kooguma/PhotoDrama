package com.loopeer.android.photodrama4android.media.render;


import android.view.View;

import com.loopeer.android.photodrama4android.media.data.VertexArray;

public abstract class ClipDrawer {
    protected static VertexArray vertexArray;
    protected static VertexArray vertexArrayRecording;

    protected int mViewWidth;
    protected int mViewHeight;
    public static float[] mVertexData;
    public static float[] mVertexDataRecording;

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

        mVertexDataRecording = new float[]{
                -1f, 1f, 0f, 1f,
                -1f, -1f, 0f, 0f,
                1f, -1f, 1f, 0f,
                1f, -1f, 1f, 0f,
                1f, 1f, 1f, 1f,
                -1f, 1f, 0f, 1f
        };
        vertexArray = new VertexArray(mVertexData);
        vertexArrayRecording = new VertexArray(mVertexDataRecording);
    }

    public abstract void drawFrame(long usedTime, float[] pMatrix, boolean isRecording);

    protected VertexArray getVertexArray(boolean isRecording) {
        if (isRecording) {
            return vertexArrayRecording;
        }
        return vertexArray;
    }
}
