package com.loopeer.android.photodrama4android.media;


import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class GLTouchListener implements View.OnTouchListener{

    private ScaleGestureDetector mScaleDetector;
    private GLSurfaceView mGLSurfaceView;
    private float mScaleFactor = 1.f;
    public float mPosY;
    public float mPosX;
    public float mLastTouchY;
    public float mLastTouchX;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleMoveListener mScaleMoveListener;

    public GLTouchListener(GLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
        mScaleDetector = new ScaleGestureDetector(mGLSurfaceView.getContext(), new ScaleListener());
    }

    public void setScaleMoveListener(ScaleMoveListener scaleMoveListener) {
        mScaleMoveListener = scaleMoveListener;
    }

    public void updateFactorXY(float scaleFactor, float x, float y) {
        mScaleFactor = scaleFactor;
        mPosX = getOpRelatX(x);
        mPosY = getOpRelatY(-y);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                mPosX += dx;
                mPosY += dy;
                gLViewMove(getRelatX(mPosX), getRelatY(mPosY));
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    private float getRelatX(float posX) {
        return posX;
    }

    private float getRelatY(float posY) {
        return posY;
    }

    private float getOpRelatX(float ratioX) {
        return ratioX;
    }

    private float getOpRelatY(float ratioY) {
        return ratioY;
    }

    private void gLViewScale(float scaleFactor) {
        if (mScaleMoveListener != null)
            mScaleMoveListener.gLViewScale(scaleFactor);
    }

    private void gLViewMove(float x, float y) {
        if (mScaleMoveListener != null)
            mScaleMoveListener.gLViewMove(x, y);
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 8.0f));
            gLViewScale(mScaleFactor);
            return true;
        }
    }

    public interface ScaleMoveListener{
        void gLViewScale(float scaleFactor);
        void gLViewMove(float x, float y);
    }
}
