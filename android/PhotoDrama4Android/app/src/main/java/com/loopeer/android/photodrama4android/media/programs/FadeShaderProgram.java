package com.loopeer.android.photodrama4android.media.programs;

import android.content.Context;

import com.loopeer.android.photodrama4android.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class FadeShaderProgram extends ShaderProgram {
    private final int uMMatrixLocation;
    private final int uVMatrixLocation;
    private final int uPMatrixLocation;
    private final int uTextureUnitLocation0;
    private final int uTextureUnitLocation1;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    private final int uProgress;

    public FadeShaderProgram(Context context) {
        super(context, R.raw.fade_vertex,
            R.raw.fade_fragment);

        uMMatrixLocation = glGetUniformLocation(program, U_MMATRIX);
        uVMatrixLocation = glGetUniformLocation(program, U_VMATRIX);
        uPMatrixLocation = glGetUniformLocation(program, U_PMATRIX);
        uTextureUnitLocation0 = glGetUniformLocation(program, U_TEXTURE_UNIT0);
        uTextureUnitLocation1 = glGetUniformLocation(program, U_TEXTURE_UNIT1);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = 
            glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        uProgress = glGetUniformLocation(program, U_PROGRESS);
    }

    public void setUniforms(float[] pMatrix, float[] vMatrix, float[] mMatrix, int textureId0, int textureId1, float progress) {
        glUniformMatrix4fv(uPMatrixLocation, 1, false, pMatrix, 0);
        glUniformMatrix4fv(uVMatrixLocation, 1, false, vMatrix, 0);
        glUniformMatrix4fv(uMMatrixLocation, 1, false, mMatrix, 0);

        glUniform1f(uProgress, progress);

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, textureId0);
        glUniform1i(uTextureUnitLocation0, 0);

        glActiveTexture(GL_TEXTURE1);

        glBindTexture(GL_TEXTURE_2D, textureId1);

        glUniform1i(uTextureUnitLocation1, 1);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}