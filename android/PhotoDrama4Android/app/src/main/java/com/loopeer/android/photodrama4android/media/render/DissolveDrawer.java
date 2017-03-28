package com.loopeer.android.photodrama4android.media.render;


import android.content.Context;
import android.view.View;

import com.loopeer.android.photodrama4android.media.cache.ShaderProgramCache;
import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.programs.DissolveShaderProgram;

import static com.loopeer.android.photodrama4android.media.Constants.BYTES_PER_FLOAT;

public class DissolveDrawer extends TransitionDrawer{

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;

    private DissolveShaderProgram textureProgram;

    public DissolveDrawer(View view, TransitionClip transitionClip) {
        super(view, transitionClip);
        mContext = view.getContext();
        textureProgram = (DissolveShaderProgram) ShaderProgramCache
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
