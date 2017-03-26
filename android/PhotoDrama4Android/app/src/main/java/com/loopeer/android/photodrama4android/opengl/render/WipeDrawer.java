package com.loopeer.android.photodrama4android.opengl.render;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.opengl.HandlerWrapper;
import com.loopeer.android.photodrama4android.opengl.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.opengl.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.opengl.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.opengl.data.VertexArray;
import com.loopeer.android.photodrama4android.opengl.model.ImageClip;
import com.loopeer.android.photodrama4android.opengl.model.ImageInfo;
import com.loopeer.android.photodrama4android.opengl.programs.ImageClipShaderProgram;
import com.loopeer.android.photodrama4android.opengl.utils.TextureHelper;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.Matrix.setIdentityM;
import static com.loopeer.android.photodrama4android.opengl.Constants.BYTES_PER_FLOAT;

public class WipeDrawer extends ClipDrawer{

    private static final String TAG = "ImageClipDrawer";

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;
    private VertexArray vertexArray;

    private Bitmap mBitmap;
    public ImageInfo mImageInfo;
    public ImageClip mImageClip;

    private ImageClipShaderProgram textureProgram;
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private int[] mCanvasTextureId = {-1};

    private int mHorizontalBlockNum = 1;
    private int mVerticalBlockNum = 1;

    public WipeDrawer(View view, ImageClip imageClip) {
        super(view);
        mContext = view.getContext();
        mImageClip = imageClip;
        textureProgram = new ImageClipShaderProgram(mContext);
    }

    public void preLoadTexture(MovieMakerGLSurfaceView glView) {
        HandlerWrapper handler = new HandlerWrapper(
                Looper.getMainLooper(),
                HandlerWrapper.TYPE_LOAD_IMAGE
                , mImageClip.path
                , new HandlerWrapper.Callback<ImageInfo>() {
            @Override
            public void onResult(ImageInfo t) {
                /*updateTexture(t);*/
                checkBitmapReady();
                VideoPlayManagerContainer.getDefault().notifyRender(mContext);
            }
        });
        glView.getTextureLoader().loadImageTexture(handler);
    }


    public void checkBitmapReady() {
        mBitmap = BitmapFactory.getInstance().getBitmapFromMemCache(mImageClip.path);
        mImageInfo = new ImageInfo(-1, mBitmap.getWidth(), mBitmap.getHeight());
        vertexArray = new VertexArray(createData());
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
        float[] result = new float[]{
                -1f, 1f, 0f, 0f,
                -1f, -1f, 0f, 1f,
                1f, -1f, 1f, 1f,
                1f, -1f, 1f, 1f,
                1f, 1f, 1f, 0f,
                -1f, 1f, 0f, 0f
        };
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
//        getTexture(usedTime);
        setIdentityM(modelMatrix, 0);
        setIdentityM(viewMatrix, 0);
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (mImageInfo == null || vertexArray == null) return;
        if (usedTime < mImageClip.startTime || usedTime > mImageClip.getEndTime()) return;
        updateViewMatrices(usedTime);
        textureProgram.useProgram();
        textureProgram.setUniforms(pMatrix, viewMatrix, modelMatrix, mCanvasTextureId[0]);
        bindData();
        draw();

    }
}
