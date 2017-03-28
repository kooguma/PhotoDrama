package com.loopeer.android.photodrama4android.media.render;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.HandlerWrapper;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.cache.TextureIdCache;
import com.loopeer.android.photodrama4android.media.data.VertexArray;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.model.ImageInfo;
import com.loopeer.android.photodrama4android.media.programs.ImageClipShaderProgram;
import com.loopeer.android.photodrama4android.media.utils.TextureHelper;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;
import static android.opengl.Matrix.setIdentityM;
import static com.loopeer.android.photodrama4android.media.Constants.BYTES_PER_FLOAT;

public class ImageClipDrawer extends ClipDrawer{

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

    public ImageClipDrawer(View view, ImageClip imageClip) {
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
        createVertex();
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

    private void getTexture(long usedTime) {
        Matrix matrix = new Matrix();
        matrix.postScale(1f * mViewHeight / mBitmap.getHeight(), 1f * mViewHeight / mBitmap.getHeight());
        matrix.postTranslate(-1f * mBitmap.getWidth() * mViewHeight / mBitmap.getHeight() / 2, -1f * mViewHeight / 2);
        matrix.postScale(mImageClip.getScaleFactor(usedTime)
                , mImageClip.getScaleFactor(usedTime));
        matrix.postTranslate(mImageClip.getTransX(usedTime), mImageClip.getTransY(usedTime));
        matrix.postTranslate(mViewWidth / 2, mViewHeight / 2);

        Bitmap localBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setFilterBitmap(true);
        localCanvas.drawBitmap(mBitmap, matrix, localPaint);
        if (mCanvasTextureId[0] != -1) {
            GLES20.glDeleteTextures(1, mCanvasTextureId, 0);
        }
        glGenTextures(1, mCanvasTextureId, 0);
        glBindTexture(GL_TEXTURE_2D, mCanvasTextureId[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, 6408, localBitmap, 0);
        localBitmap.recycle();
        if (usedTime > mImageClip.getEndTime()) {
            TextureIdCache.getInstance().addIdToCache(mImageClip.getEndTime() + 1, mCanvasTextureId[0]);
        }
        if (usedTime < mImageClip.startTime) {
            TextureIdCache.getInstance().addIdToCache(mImageClip.startTime - 1, mCanvasTextureId[0]);
        }
    }

    private void updateViewMatrices(long usedTime) {
        getTexture(usedTime);
        setIdentityM(modelMatrix, 0);
        setIdentityM(viewMatrix, 0);
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (mImageInfo == null || vertexArray == null) return;
        if (usedTime < mImageClip.startWithPreTransitionTime || usedTime > mImageClip.endWithNextTransitionTime) return;
        updateViewMatrices(usedTime);
        if (usedTime < mImageClip.startTime || usedTime > mImageClip.getEndTime()) return;
        textureProgram.useProgram();
        textureProgram.setUniforms(pMatrix, viewMatrix, modelMatrix, mCanvasTextureId[0]);
        bindData();
        draw();
    }
}
