package com.loopeer.android.photodrama4android.opengl.render;


import android.view.View;

import com.loopeer.android.photodrama4android.opengl.cache.TextureIdCache;
import com.loopeer.android.photodrama4android.opengl.model.TransitionClip;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.Matrix.setIdentityM;

public abstract class TransitionDrawer extends ClipDrawer {

    public TransitionClip mTransitionClip;

    protected final float[] modelMatrix = new float[16];
    protected final float[] viewMatrix = new float[16];
    protected int mTextureIdPre;
    protected int mTextureIdNext;

    public TransitionDrawer(View view, TransitionClip transitionClip) {
        super(view);
        mTransitionClip = transitionClip;
    }

    protected void draw() {
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    private void updateViewMatrices(long usedTime) {
        mTextureIdPre = TextureIdCache.getInstance().getTextureId(mTransitionClip.startTime);
        mTextureIdNext = TextureIdCache.getInstance().getTextureId(mTransitionClip.getEndTime());
        setIdentityM(modelMatrix, 0);
        setIdentityM(viewMatrix, 0);
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (usedTime < mTransitionClip.startTime || usedTime > mTransitionClip.getEndTime()) return;
        updateViewMatrices(usedTime);
        updateProgramBindData(usedTime, pMatrix);
        draw();
    }

    abstract public void updateProgramBindData(long usedTime, float[] pMatrix);

    protected float getProgress(long usedTime) {
        return 1f * (usedTime - mTransitionClip.startTime) / mTransitionClip.showTime;
    }
}
