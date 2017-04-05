package com.loopeer.android.photodrama4android.media.render;

import android.content.Context;

import com.loopeer.android.photodrama4android.media.IRendererWorker;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.media.model.Drama;

import org.wysaid.nativePort.FrameRecorder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.setIdentityM;

public class GLRenderWorker implements IRendererWorker {

    private Drama mDrama;
    private Context mContext;
    private VideoClipProcessor mImageClipProcessor;
    private final float[] projectionMatrix = new float[16];
    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;
    private boolean mWillRecord;
    protected FrameRecorder mFrameRecorder;
    private boolean mIsRecording;

    public GLRenderWorker(Context context, Drama drama, MovieMakerGLSurfaceView view) {
        mContext = context;
        mMovieMakerGLSurfaceView = view;
        mDrama = drama;
    }

    public Drama getDrama() {
        return mDrama;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mImageClipProcessor = new VideoClipProcessor(mMovieMakerGLSurfaceView);
        mImageClipProcessor.updateData(mDrama.videoGroup);

        if (mWillRecord) {
            mFrameRecorder = new FrameRecorder();
            int height = mMovieMakerGLSurfaceView.getHeight();
            height = height % 2 != 0 ? height - 1 : height;
            if(!mFrameRecorder.init(mMovieMakerGLSurfaceView.getWidth()
                    , height
                    , mMovieMakerGLSurfaceView.getWidth()
                    , height)) {

            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        if (mWillRecord) {
            mFrameRecorder.srcResize(width, height % 2 != 0 ? height - 1 : height);
        }

    }

    @Override
    public void drawFrame(Context context, GL10 gl, long usedTime) {
        glClear(GL_COLOR_BUFFER_BIT);
        setIdentityM(projectionMatrix, 0);
        mImageClipProcessor.drawFrame(usedTime, projectionMatrix, mIsRecording);
        if (mIsRecording) {
            mFrameRecorder.runProc(usedTime);
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
        mMovieMakerGLSurfaceView.queueEvent(() -> {
            if (mFrameRecorder.startRecording(30, filename)) {
                mIsRecording = true;
            }
        });
    }

    public synchronized void endRecording() {
        mIsRecording = false;
        if(mFrameRecorder == null) {
            return;
        }
        mMovieMakerGLSurfaceView.queueEvent(() -> {
            if (mFrameRecorder != null)
                mFrameRecorder.endRecording(true);
        });
    }

    public void setWillRecord(boolean willRecord) {
        mWillRecord = willRecord;
    }
}
