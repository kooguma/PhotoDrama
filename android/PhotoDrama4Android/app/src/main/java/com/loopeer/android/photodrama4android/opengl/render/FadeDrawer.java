package com.loopeer.android.photodrama4android.opengl.render;


import android.content.Context;
import android.view.View;

import com.loopeer.android.photodrama4android.opengl.cache.ShaderProgramCache;
import com.loopeer.android.photodrama4android.opengl.model.TransitionClip;
import com.loopeer.android.photodrama4android.opengl.programs.FadeShaderProgram;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static com.loopeer.android.photodrama4android.opengl.Constants.BYTES_PER_FLOAT;

public class FadeDrawer extends TransitionDrawer{

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;

    private FadeShaderProgram textureProgram;

    public FadeDrawer(View view, TransitionClip transitionClip) {
        super(view, transitionClip);
        mContext = view.getContext();
        textureProgram = (FadeShaderProgram) ShaderProgramCache
                .getInstance()
                .getTextureId(String.valueOf(mTransitionClip.transitionType.getValue()));
        createVertex();
    }

    @Override
    public void updateProgramBindData(long usedTime, float[] pMatrix) {
        textureProgram.useProgram();
        textureProgram.setUniforms(pMatrix, viewMatrix, modelMatrix, mTextureIdPre, mTextureIdNext
                , getProgress(usedTime));
        bindData();
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (usedTime < mTransitionClip.startTime || usedTime > mTransitionClip.getEndTime()) return;
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        updateViewMatrices(usedTime);
        updateProgramBindData(usedTime, pMatrix);
        draw();
        glDisable(GL_BLEND);
/*
        glDisable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);*/

    }

    private void bindData() {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }
}
