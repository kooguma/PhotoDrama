package com.loopeer.android.photodrama4android.media.render;


import android.content.Context;
import android.view.View;

import com.loopeer.android.photodrama4android.media.cache.ShaderProgramCache;
import com.loopeer.android.photodrama4android.media.cache.TextureIdCache;
import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.programs.ImageClipShaderProgram;

import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.loopeer.android.photodrama4android.media.Constants.BYTES_PER_FLOAT;

public class SlideDrawer extends TransitionDrawer{

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;

    private ImageClipShaderProgram textureProgram0;
    private ImageClipShaderProgram textureProgram1;

    protected final float[] modelMatrix1 = new float[16];
    protected final float[] viewMatrix1 = new float[16];

    public SlideDrawer(View view, TransitionClip transitionClip) {
        super(view, transitionClip);
        mContext = view.getContext();
        textureProgram0 = (ImageClipShaderProgram) ShaderProgramCache
                .getInstance()
                .getTextureId(String.valueOf(mTransitionClip.transitionType.getValue()) + "_0");

        textureProgram1 = (ImageClipShaderProgram) ShaderProgramCache
                .getInstance()
                .getTextureId(String.valueOf(mTransitionClip.transitionType.getValue()) + "_1");
        createVertex();
    }

    public void updateProgramBindData0(long usedTime, float[] pMatrix) {
        textureProgram0.useProgram();
        textureProgram0.setUniforms(pMatrix, viewMatrix, modelMatrix, mTextureIdPre);
        bindData0();
    }

    public void updateProgramBindData(long usedTime, float[] pMatrix) {
        textureProgram1.useProgram();
        textureProgram1.setUniforms(pMatrix, viewMatrix1, modelMatrix1, mTextureIdNext);
        bindData1();
    }

    private void bindData0() {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram0.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram0.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    private void bindData1() {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram1.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram1.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    protected void updateViewMatrices(long usedTime) {
        mTextureIdPre = TextureIdCache.getInstance().getTextureId(mTransitionClip.startTime);
        mTextureIdNext = TextureIdCache.getInstance().getTextureId(mTransitionClip.getEndTime());
        setIdentityM(modelMatrix, 0);
        setIdentityM(viewMatrix, 0);

        setIdentityM(modelMatrix1, 0);
        setIdentityM(viewMatrix1, 0);
        translateM(viewMatrix1, 0, 0f, -2f * (1f - getProgress(usedTime)), 0f);
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (usedTime < mTransitionClip.startTime || usedTime > mTransitionClip.getEndTime()) return;
        updateViewMatrices(usedTime);
        updateProgramBindData0(usedTime, pMatrix);
        draw();
        updateProgramBindData(usedTime, pMatrix);
        draw();
    }

}
