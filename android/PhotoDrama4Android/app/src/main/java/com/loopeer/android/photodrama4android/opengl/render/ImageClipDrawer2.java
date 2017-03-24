package com.loopeer.android.photodrama4android.opengl.render;


import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.opengl.HandlerWrapper;
import com.loopeer.android.photodrama4android.opengl.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.opengl.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.opengl.data.VertexArray;
import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.model.ImageInfo;
import com.loopeer.android.photodrama4android.opengl.programs.ImageClipShaderProgram;
import com.loopeer.android.photodrama4android.opengl.utils.TextureHelper;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.loopeer.android.photodrama4android.opengl.Constants.BYTES_PER_FLOAT;

public class ImageClipDrawer2 {

    private static final String TAG = "ImageClipDrawer";

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;
    private VertexArray vertexArray;

    public ImageInfo mImageInfo;
    public ImageClip mImageClip;

    private ImageClipShaderProgram textureProgram;
    private final float[] modelMatrix = new float[16];
    private final float[] moveMatrix = new float[16];
    private final float[] viewMatrix = new float[16];


    private int mHorizontalBlockNum = 1;
    private int mVerticalBlockNum = 1;

    public ImageClipDrawer2(Context context, ImageClip imageClip) {
        mContext = context;
        mImageClip = imageClip;
        textureProgram = new ImageClipShaderProgram(context);
    }

    public void preLoadTexture(MovieMakerGLSurfaceView glView) {
        HandlerWrapper handler = new HandlerWrapper(
                Looper.getMainLooper(),
                HandlerWrapper.TYPE_LOAD_IMAGE
                , mImageClip.path
                , new HandlerWrapper.Callback<ImageInfo>() {
            @Override
            public void onResult(ImageInfo t) {
                updateTexture(t);
                VideoPlayManagerContainer.getDefault().notifyRender(mContext);
            }
        });
        glView.getTextureLoader().loadImageTexture(handler);
    }


    public void checkBitmapReady() {

    }

    private void updateTexture(ImageInfo t) {
        if (BuildConfig.LOG_ON) {
            Log.e(TAG, t.toString());
        }
        mImageInfo = t;
        vertexArray = new VertexArray(createData());
    }

    public void updateTexture() {
        mImageInfo = TextureHelper.loadTexture(mContext, mImageClip.path);
        vertexArray = new VertexArray(createData());
    }

    private float[] createData() {
        float halfWidth = 1f * mImageInfo.width / mImageInfo.height;
        float hMax = 1f;
        float wMin = -halfWidth;
        float wCMin = 0f;
        float hCMin = 0f;
        float itemWidth = halfWidth * 2 / mHorizontalBlockNum;
        float itemHeight = 1f * 2 / mVerticalBlockNum;
        float itemCWidth = 1f / mHorizontalBlockNum;
        float itemCHeight = 1f / mVerticalBlockNum;
        float[] result = new float[mHorizontalBlockNum * mVerticalBlockNum * 6 * 4];
        for (int i = 0; i < mHorizontalBlockNum; i++) {
            for (int j = 0; j < mVerticalBlockNum; j++) {
                float topLeftPX = wMin + itemWidth * i;
                float topLeftPY = hMax - itemHeight * j;
                float topLeftCS = wCMin + itemCWidth * i;
                float topLeftCT = hCMin + itemCHeight * j;

                int index = (i * mVerticalBlockNum + j) * 6 * 4;
                result[index + 0] = topLeftPX;
                result[index + 1] = topLeftPY;
                result[index + 2] = topLeftCS;
                result[index + 3] = topLeftCT;

                //bottomleft
                result[index + 4] = topLeftPX;
                result[index + 5] = topLeftPY - itemHeight;
                result[index + 6] = topLeftCS;
                result[index + 7] = topLeftCT + itemCHeight;

                //bottomright
                result[index + 8] = topLeftPX + itemWidth;
                result[index + 9] = topLeftPY - itemHeight;
                result[index + 10] = topLeftCS + itemCWidth;
                result[index + 11] = topLeftCT + itemCHeight;

                //bottomright
                result[index + 12] = topLeftPX + itemWidth;
                result[index + 13] = topLeftPY - itemHeight;
                result[index + 14] = topLeftCS + itemCWidth;
                result[index + 15] = topLeftCT + itemCHeight;

                //topright
                result[index + 16] = topLeftPX + itemWidth;
                result[index + 17] = topLeftPY;
                result[index + 18] = topLeftCS + itemCWidth;
                result[index + 19] = topLeftCT;

                //topleft
                result[index + 20] = topLeftPX;
                result[index + 21] = topLeftPY;
                result[index + 22] = topLeftCS;
                result[index + 23] = topLeftCT;
            }
        }
        int i = 20;
        return result;
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

        setIdentityM(moveMatrix, 0);
        scaleM(moveMatrix
                , 0
                , mImageClip.getScaleFactor(usedTime)
                , mImageClip.getScaleFactor(usedTime)
                , 0f);
        multiplyMM(viewMatrix, 0, moveMatrix, 0, viewMatrix, 0);

        setIdentityM(moveMatrix, 0);
        translateM(moveMatrix, 0, mImageClip.getTransX(usedTime), mImageClip.getTransY(usedTime), 0f);
        multiplyMM(viewMatrix, 0, moveMatrix, 0, viewMatrix, 0);
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (mImageInfo == null || vertexArray == null) return;
        if (usedTime < mImageClip.startTime || usedTime > mImageClip.getEndTime()) return;
        updateViewMatrices(usedTime);
        textureProgram.useProgram();
        textureProgram.setUniforms(pMatrix, viewMatrix, modelMatrix, mImageInfo.textureObjectId);
        bindData();
        draw();

    }
}
