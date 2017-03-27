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

import com.loopeer.android.photodrama4android.opengl.utils.ShaderHelper;
import com.loopeer.android.photodrama4android.opengl.utils.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

public abstract class ShaderProgram {

    protected static final String U_MMATRIX = "u_MMatrix";
    protected static final String U_VMATRIX = "u_VMatrix";
    protected static final String U_PMATRIX = "u_PMatrix";
    protected static final String U_TEXTURE_UNIT0 = "u_TextureUnit0";
    protected static final String U_TEXTURE_UNIT1 = "u_TextureUnit1";

    protected static final String A_POSITION = "a_Position";
    protected static final String A_THRESHOLD = "a_Threshold";

    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected static final String U_PROGRESS = "u_Progress";
    protected static final String U_DIRECTION = "u_Direction";

    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(
            TextResourceReader.readTextFileFromResource(
                context, vertexShaderResourceId),
            TextResourceReader.readTextFileFromResource(
                context, fragmentShaderResourceId));
    }        

    public void useProgram() {
        glUseProgram(program);
    }
}
