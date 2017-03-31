package com.loopeer.android.photodrama4android.media.render;

import android.content.Context;
import android.util.Log;

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
import static org.wysaid.myUtils.FileUtil.LOG_TAG;

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
            if(!mFrameRecorder.init(mMovieMakerGLSurfaceView.getWidth()
                    , mMovieMakerGLSurfaceView.getHeight()
                    , mMovieMakerGLSurfaceView.getWidth()
                    , mMovieMakerGLSurfaceView.getHeight())) {

            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        if (mWillRecord) {
            mFrameRecorder.srcResize(width, height);
        }

    }

    @Override
    public void drawFrame(Context context, GL10 gl, long usedTime) {
        glClear(GL_COLOR_BUFFER_BIT);
        setIdentityM(projectionMatrix, 0);
        mImageClipProcessor.drawFrame(usedTime, projectionMatrix);
        if (mIsRecording) {
            mFrameRecorder.runProc();
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

    public synchronized void startRecording(final String filename /*final StartRecordingCallback recordingCallback*/) {
        mMovieMakerGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {

                if (mFrameRecorder == null) {
                    Log.e(LOG_TAG, "Error: startRecording after release!!");
                    /*if (recordingCallback != null) {
//                        recordingCallback.startRecordingOver(false);
                    }*/
                    return;
                }

                if (!mFrameRecorder.startRecording(30, filename)) {
                    /*if (recordingCallback != null)
//                        recordingCallback.startRecordingOver(false);
                    return;*/
                    mIsRecording = true;

                }

               /* synchronized (mRecordStateLock) {
                    mShouldRecord = true;
                    mAudioAddRunnable = new AudioAddRunnable();
                    mAudioThread = new Thread(mAudioAddRunnable);
                    mAudioThread.start();
                }*/
            }
        });
    }

    public synchronized void endRecording(/*final EndRecordingCallback callback*/) {
        Log.i(LOG_TAG, "notify quit...");
        mIsRecording = false;
       /* synchronized (mRecordStateLock) {
            mShouldRecord = false;
        }*/

        if(mFrameRecorder == null) {
            Log.e(LOG_TAG, "Error: endRecording after release!!");
            return;
        }

//        joinAudioRecording();

        mMovieMakerGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mFrameRecorder != null)
                    mFrameRecorder.endRecording(true);
                /*if (callback != null) {
                    callback.endRecordingOK();
                }*/
            }
        });
    }

    public void setWillRecord(boolean willRecord) {
        mWillRecord = willRecord;
    }
}
