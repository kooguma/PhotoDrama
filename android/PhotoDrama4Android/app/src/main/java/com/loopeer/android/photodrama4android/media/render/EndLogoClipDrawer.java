package com.loopeer.android.photodrama4android.media.render;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.TextureView;

import com.loopeer.android.photodrama4android.media.HandlerWrapper;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.cache.ShaderProgramCache;
import com.loopeer.android.photodrama4android.media.model.EndLogoClip;
import com.loopeer.android.photodrama4android.media.programs.ImageClipShaderProgram;

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

public class EndLogoClipDrawer extends ClipDrawer{

    private static final String TAG = "ImageClipDrawer";

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;

    private Bitmap mLogoBitmap;
    private Bitmap mTextBitmap;
    public EndLogoClip mEndLogoClip;

    private ImageClipShaderProgram textureProgram;
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private int[] mCanvasTextureId = {-1};

    private int mHorizontalBlockNum = 1;
    private int mVerticalBlockNum = 1;

    private float mViewScaleFactor;

    private final float SCREEN_IMAGE_LOGO_WIDTH_FACTOR = 148f / 1334f;
    private final float SCREEN_IMAGE_LOGO_TOP_FACTOR = 234f / 750f;
    private final float SCREEN_IMAGE_TEXT_TOP_FACTOR = 440f / 750f;

    public EndLogoClipDrawer(TextureView view, EndLogoClip endLogoClip) {
        super(view);
        mContext = view.getContext();
        mEndLogoClip = endLogoClip;

        textureProgram = (ImageClipShaderProgram) ShaderProgramCache
                .getInstance()
                .getTextureId(ShaderProgramCache.NORMAL_IMAGE_PROGRAM_KEY);
    }

    public void preLoadTexture(TextureView glView) {
        HandlerWrapper handler = new HandlerWrapper(
                Looper.getMainLooper(),
                HandlerWrapper.TYPE_LOAD_IMAGE_RES
                , mEndLogoClip.logoRes
                , t -> {
            checkLogoBitmapReady();
        });
//        glView.getTextureLoader().loadImageTexture(handler);
        HandlerWrapper handler2 = new HandlerWrapper(
                Looper.getMainLooper(),
                HandlerWrapper.TYPE_LOAD_IMAGE_RES
                , mEndLogoClip.textRes
                , t -> {
            checkTextBitmapReady();
        });
//        glView.getTextureLoader().loadImageTexture(handler2);
    }

    public void checkLogoBitmapReady() {
        mLogoBitmap = BitmapFactory.getInstance().getBitmapFromMemCache(String.valueOf(mEndLogoClip.logoRes));
        if (mLogoBitmap == null) return;
        mViewScaleFactor = SCREEN_IMAGE_LOGO_WIDTH_FACTOR * mViewWidth / mLogoBitmap.getWidth();
    }

    public void checkTextBitmapReady() {
        mTextBitmap = BitmapFactory.getInstance().getBitmapFromMemCache(String.valueOf(mEndLogoClip.textRes));
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
        Matrix matrixLogo = new Matrix();
        matrixLogo.postScale(mViewScaleFactor, mViewScaleFactor);
        matrixLogo.postTranslate(mViewWidth / 2 - mViewScaleFactor * mLogoBitmap.getWidth() / 2, mViewHeight * SCREEN_IMAGE_LOGO_TOP_FACTOR);
        matrixLogo.postRotate(mEndLogoClip.getDegree((int) usedTime), mViewWidth / 2, mViewHeight * SCREEN_IMAGE_LOGO_TOP_FACTOR + mLogoBitmap.getHeight() * mViewScaleFactor / 2);

        Matrix matrixText = new Matrix();
        matrixText.postScale(mViewScaleFactor, mViewScaleFactor);
        matrixText.postTranslate(mViewWidth / 2 - mViewScaleFactor * mTextBitmap.getWidth() / 2, mViewHeight * SCREEN_IMAGE_TEXT_TOP_FACTOR);

        Bitmap localBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint logoPaint = new Paint();
        Paint textPaint = new Paint();

        textPaint.setAlpha(mEndLogoClip.getAlpha((int) usedTime));
        localCanvas.drawColor(ContextCompat.getColor(mMovieMakerTextureView.getContext(), android.R.color.white));
        logoPaint.setFilterBitmap(true);
        textPaint.setFilterBitmap(true);
        localCanvas.drawBitmap(mLogoBitmap, matrixLogo, logoPaint);
        localCanvas.drawBitmap(mTextBitmap, matrixText, textPaint);
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
    }

    private void updateViewMatrices(long usedTime) {
        getTexture(usedTime);
        setIdentityM(modelMatrix, 0);
        setIdentityM(viewMatrix, 0);
    }

    public void drawFrame(long usedTime, float[] pMatrix) {
        if (mLogoBitmap == null || mLogoBitmap.isRecycled() || mTextBitmap == null || mTextBitmap.isRecycled()) {
            preLoadTexture(mMovieMakerTextureView);
            return;
        }
        if (usedTime < mEndLogoClip.startTime || usedTime > mEndLogoClip.getEndTime()) return;
        updateViewMatrices(usedTime);
        textureProgram.useProgram();
        textureProgram.setUniforms(pMatrix, viewMatrix, modelMatrix, mCanvasTextureId[0]);
        bindData();
        draw();
    }
}
