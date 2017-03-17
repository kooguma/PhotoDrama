/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.loopeer.android.photodrama4android.opengl.programs;

import android.content.Context;

import com.loopeer.android.photodrama4android.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ImageClipShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMMatrixLocation;
    private final int uVMatrixLocation;
    private final int uPMatrixLocation;
    private final int uTextureUnitLocation0;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public ImageClipShaderProgram(Context context) {
        super(context, R.raw.image_clip_vertex,
                R.raw.image_clip_fragment);

        // Retrieve uniform locations for the shader program.
        uMMatrixLocation = glGetUniformLocation(program, U_MMATRIX);
        uVMatrixLocation = glGetUniformLocation(program, U_VMATRIX);
        uPMatrixLocation = glGetUniformLocation(program, U_PMATRIX);
        uTextureUnitLocation0 = glGetUniformLocation(program, U_TEXTURE_UNIT0);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation =
                glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] pMatrix, float[] vMatrix, float[] mMatrix, int textureId0) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uPMatrixLocation, 1, false, pMatrix, 0);
        glUniformMatrix4fv(uVMatrixLocation, 1, false, vMatrix, 0);
        glUniformMatrix4fv(uMMatrixLocation, 1, false, mMatrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId0);
        glUniform1i(uTextureUnitLocation0, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
