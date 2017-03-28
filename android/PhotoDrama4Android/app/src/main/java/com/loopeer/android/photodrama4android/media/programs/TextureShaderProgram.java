/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.loopeer.android.photodrama4android.media.programs;

import android.content.Context;

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

public class TextureShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMMatrixLocation;
    private final int uVMatrixLocation;
    private final int uPMatrixLocation;
    private final int uTextureUnitLocation0;
    private final int uTextureUnitLocation1;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    private final int uProgress;
    private final int uDirection;

    private final int aThreshold;

    public TextureShaderProgram(Context context) {
        super(context, 0,
                0);

        // Retrieve uniform locations for the shader program.
        uMMatrixLocation = glGetUniformLocation(program, U_MMATRIX);
        uVMatrixLocation = glGetUniformLocation(program, U_VMATRIX);
        uPMatrixLocation = glGetUniformLocation(program, U_PMATRIX);
        uTextureUnitLocation0 = glGetUniformLocation(program, U_TEXTURE_UNIT0);
        uTextureUnitLocation1 = glGetUniformLocation(program, U_TEXTURE_UNIT1);

        aThreshold = glGetAttribLocation(program, A_THRESHOLD);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation =
                glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        uProgress = glGetUniformLocation(program, U_PROGRESS);
        uDirection = glGetUniformLocation(program, U_DIRECTION);
    }

    public void setUniforms(float[] pMatrix, float[] vMatrix, float[] mMatrix, int textureId0, int textureId1, float progress, int direction) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uPMatrixLocation, 1, false, pMatrix, 0);
        glUniformMatrix4fv(uVMatrixLocation, 1, false, vMatrix, 0);
        glUniformMatrix4fv(uMMatrixLocation, 1, false, mMatrix, 0);

        glUniform1f(uProgress, progress);
        glUniform1i(uDirection, direction);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId0);
        glUniform1i(uTextureUnitLocation0, 0);

        glActiveTexture(GL_TEXTURE1);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId1);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation1, 1);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }


    public int getaThreshold() {
        return aThreshold;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
