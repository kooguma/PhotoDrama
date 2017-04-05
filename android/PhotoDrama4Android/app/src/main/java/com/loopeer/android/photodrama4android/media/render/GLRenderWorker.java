package com.loopeer.android.photodrama4android.media.render;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.loopeer.android.photodrama4android.media.IRendererWorker;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.recorder.TextureMovieEncoder;
import com.loopeer.android.photodrama4android.media.recorder.VideoEncoderCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglHelperLocal;
import com.loopeer.android.photodrama4android.media.recorder.gles.FullFrameRect;
import com.loopeer.android.photodrama4android.media.recorder.gles.GlUtil;
import com.loopeer.android.photodrama4android.media.recorder.gles.Texture2dProgram;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.setIdentityM;

public class GLRenderWorker implements IRendererWorker {

    private static final String TAG = "GLRenderWorker";

    private Drama mDrama;
    private Context mContext;
    private VideoClipProcessor mImageClipProcessor;
    private final float[] projectionMatrix = new float[16];
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;
    private boolean mIsRecording;
    private EglHelperLocal mEglHelperLocal;

    private final float[] mIdentityMatrix;
    private EglCore mEglCore;

    // Used for off-screen rendering.
    private int mOffscreenTexture;
    private int mFramebuffer;
    private int mDepthBuffer;
    private FullFrameRect mFullScreen;

    // Used for recording.
    private WindowSurface mInputWindowSurface;
    private TextureMovieEncoder mVideoEncoder;
    private Rect mVideoRect;

    public GLRenderWorker(Context context, Drama drama, MovieMakerGLSurfaceView view) {
        mContext = context;
        mMovieMakerGLSurfaceView = view;
        mDrama = drama;
        mIdentityMatrix = new float[16];
        Matrix.setIdentityM(mIdentityMatrix, 0);
        mVideoRect = new Rect();
    }

    public Drama getDrama() {
        return mDrama;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mImageClipProcessor = new VideoClipProcessor(mMovieMakerGLSurfaceView);
        mImageClipProcessor.updateData(mDrama.videoGroup);
        mEglHelperLocal = mMovieMakerGLSurfaceView.getEglHelperLocal();
        mEglHelperLocal.makeCurrent();
        mEglCore = new EglCore(mEglHelperLocal.mEgl, mEglHelperLocal.mEglContext, EglCore.FLAG_RECORDABLE | EglCore.FLAG_TRY_GLES3);
        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        prepareFramebuffer(width, height);
        glViewport(0, 0, width, height);
    }

    @Override
    public void drawFrame(Context context, GL10 gl, long usedTime) {
        if (!mIsRecording) {
            glClear(GL_COLOR_BUFFER_BIT);
            setIdentityM(projectionMatrix, 0);
            mImageClipProcessor.drawFrame(usedTime, projectionMatrix);
        } else {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
            GlUtil.checkGlError("glBindFramebuffer");
            glClear(GL_COLOR_BUFFER_BIT);
            setIdentityM(projectionMatrix, 0);
            mImageClipProcessor.drawFrame(usedTime, projectionMatrix);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GlUtil.checkGlError("glBindFramebuffer");
            mFullScreen.drawFrame(mOffscreenTexture, mIdentityMatrix);
            mEglHelperLocal.swapBuffers();
            mVideoEncoder.frameAvailableSoon();
            mInputWindowSurface.makeCurrent();
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glViewport(mVideoRect.left, mVideoRect.top,
                    mVideoRect.width(), mVideoRect.height());
            mFullScreen.drawFrame(mOffscreenTexture, mIdentityMatrix);
            mInputWindowSurface.swapBuffers();
            GLES20.glViewport(0, 0, mEglHelperLocal.getWidth(), mEglHelperLocal.getHeight());
            mEglHelperLocal.makeCurrent();

        }
    }

    public void updateDrama(Drama drama) {
        mDrama = drama;
    }

    public void updateAll() {
        mImageClipProcessor.updateData(mDrama.videoGroup);
    }

    public void refreshTransitionRender() {
        if (mImageClipProcessor != null) mImageClipProcessor.updateTransitionClipRenders();
    }

    public void refreshSubtitleRender() {
        if (mImageClipProcessor != null) mImageClipProcessor.updateSubtitleClipRenders();
    }

    public synchronized void startRecording(final String filename) {
        startEncoder(filename);
        mIsRecording = true;
    }

    public synchronized void endRecording() {
        if (mIsRecording) {
            mIsRecording = false;
            stopEncoder();
        }
    }

    private void prepareFramebuffer(int width, int height) {
        GlUtil.checkGlError("prepareFramebuffer start");

        int[] values = new int[1];
        GLES20.glGenTextures(1, values, 0);
        GlUtil.checkGlError("glGenTextures");
        mOffscreenTexture = values[0];   // expected > 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);
        GlUtil.checkGlError("glBindTexture " + mOffscreenTexture);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GlUtil.checkGlError("glTexParameter");
        GLES20.glGenFramebuffers(1, values, 0);
        GlUtil.checkGlError("glGenFramebuffers");
        mFramebuffer = values[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
        GlUtil.checkGlError("glBindFramebuffer " + mFramebuffer);

        // Create a depth buffer and bind it.
        GLES20.glGenRenderbuffers(1, values, 0);
        GlUtil.checkGlError("glGenRenderbuffers");
        mDepthBuffer = values[0];    // expected > 0
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GlUtil.checkGlError("glBindRenderbuffer " + mDepthBuffer);

        // Allocate storage for the depth buffer.
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                width, height);
        GlUtil.checkGlError("glRenderbufferStorage");

        // Attach the depth buffer and the texture (color buffer) to the framebuffer object.
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GlUtil.checkGlError("glFramebufferRenderbuffer");
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);
        GlUtil.checkGlError("glFramebufferTexture2D");

        // See if GLES is happy with all this.
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GlUtil.checkGlError("prepareFramebuffer done");
    }

    private void startEncoder(String fileName) {
        Log.d(TAG, "starting to record");
        // Record at 1280x720, regardless of the window dimensions.  The encoder may
        // explode if given "strange" dimensions, e.g. a width that is not a multiple
        // of 16.  We can box it as needed to preserve dimensions.
        final int BIT_RATE = 4000000;   // 4Mbps
        final int VIDEO_WIDTH = 1280;
        final int VIDEO_HEIGHT = 720;
        int windowWidth = mMovieMakerGLSurfaceView.getWidth();
        int windowHeight = mMovieMakerGLSurfaceView.getHeight();
        float windowAspect = (float) windowHeight / (float) windowWidth;
        int outWidth, outHeight;
        if (VIDEO_HEIGHT > VIDEO_WIDTH * windowAspect) {
            // limited by narrow width; reduce height
            outWidth = VIDEO_WIDTH;
            outHeight = (int) (VIDEO_WIDTH * windowAspect);
        } else {
            // limited by short height; restrict width
            outHeight = VIDEO_HEIGHT;
            outWidth = (int) (VIDEO_HEIGHT / windowAspect);
        }
        int offX = (VIDEO_WIDTH - outWidth) / 2;
        int offY = (VIDEO_HEIGHT - outHeight) / 2;
        mVideoRect.set(offX, offY, offX + outWidth, offY + outHeight);
        Log.d(TAG, "Adjusting window " + windowWidth + "x" + windowHeight +
                " to +" + offX + ",+" + offY + " " +
                mVideoRect.width() + "x" + mVideoRect.height());

        VideoEncoderCore encoderCore;
        try {
            encoderCore = new VideoEncoderCore(VIDEO_WIDTH, VIDEO_HEIGHT,
                    BIT_RATE, new File(fileName));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mInputWindowSurface = new WindowSurface(mEglCore, encoderCore.getInputSurface(), true);
        mVideoEncoder = new TextureMovieEncoder(encoderCore);
    }

    private void stopEncoder() {
        if (mVideoEncoder != null) {
            mVideoEncoder.stopRecording();
            mVideoEncoder = null;
        }
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
    }

}
