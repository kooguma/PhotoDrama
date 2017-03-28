package com.loopeer.android.photodrama4android.opengl.render;


import android.content.Context;
import android.os.Looper;
import android.view.View;

import com.loopeer.android.photodrama4android.opengl.HandlerWrapper;
import com.loopeer.android.photodrama4android.opengl.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.opengl.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.opengl.model.SubtitleClip;
import com.loopeer.android.photodrama4android.opengl.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.opengl.programs.ImageClipShaderProgram;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.Matrix.setIdentityM;
import static com.loopeer.android.photodrama4android.opengl.Constants.BYTES_PER_FLOAT;

public class SubtitleClipDrawer extends ClipDrawer{

    private static final String TAG = "SubtitleClipDrawer";

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;
    public SubtitleInfo mSubtitleInfo;
    public SubtitleClip mSubtitleClip;

    private ImageClipShaderProgram textureProgram;
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private int mHorizontalBlockNum = 1;
    private int mVerticalBlockNum = 1;

    public SubtitleClipDrawer(View view, SubtitleClip subtitleClip) {
        super(view);
        mContext = view.getContext();
        mSubtitleClip = subtitleClip;
        textureProgram = new ImageClipShaderProgram(mContext);
        createVertex();
    }

    public void preLoadTexture(MovieMakerGLSurfaceView glView) {
        HandlerWrapper handler = new HandlerWrapper(
                Looper.getMainLooper(),
                HandlerWrapper.TYPE_LOAD_SUBTITLE
                , new SubtitleInfo(glView.getWidth(), glView.getHeight(), mSubtitleClip.content)
                , new HandlerWrapper.Callback<SubtitleInfo>() {
            @Override
            public void onResult(SubtitleInfo t) {
                mSubtitleInfo = t;
                VideoPlayManagerContainer.getDefault().notifyRender(mContext);
            }
        });
        glView.getTextureLoader().loadImageTexture(handler);
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

    private void draw() {
        glDrawArrays(GL_TRIANGLES, 0, mHorizontalBlockNum * mVerticalBlockNum * 6);
    }

    private void updateViewMatrices(long usedTime) {
        setIdentityM(modelMatrix, 0);
        setIdentityM(viewMatrix, 0);
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (mSubtitleInfo == null || vertexArray == null) return;
        if (usedTime < mSubtitleClip.startTime || usedTime > mSubtitleClip.getEndTime()) return;
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        updateViewMatrices(usedTime);
        textureProgram.useProgram();
        textureProgram.setUniforms(pMatrix, viewMatrix, modelMatrix, mSubtitleInfo.textureObjectId);
        bindData();
        draw();
        glDisable(GL_BLEND);
    }
}
