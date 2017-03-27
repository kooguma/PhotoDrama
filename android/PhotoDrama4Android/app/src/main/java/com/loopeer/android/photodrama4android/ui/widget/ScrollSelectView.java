package com.loopeer.android.photodrama4android.ui.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class ScrollSelectView extends ViewGroup {

    public float mPosY;
    public float mPosX;
    public float mLastTouchY;
    public float mLastTouchX;
    private int mActivePointerId = INVALID_POINTER_ID;

    private Paint mPaint;
    private int mMiddlePos;

    private float mMiddleLineWidth = 2f;
    private int mTotalContentWidth;
    private Adapter mAdapter;
    private final ChildViewDataObserver mObserver = new ChildViewDataObserver();

    public ScrollSelectView(Context context) {
        this(context, null);
    }

    public ScrollSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollSelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureChild(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        mMiddlePos = getWidth() / 2;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChild();
    }

    private void layoutChild() {
        int childTop = getPaddingTop();
        int childLeft = mMiddlePos;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (i != 0) {
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            } else {
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }
            childLeft += childWidth;
        }
        mTotalContentWidth = childLeft - mMiddlePos;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
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
                scrollContent();
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

    private void scrollContent() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setTranslationX(clampTrans(mPosX));
        }
    }

    private float clampTrans(float transX) {
        if (transX >= 0) {
            return 0;
        }
        if (transX <= -getTotalLength())
            return -getTotalLength();
        return transX;
    }

    private float getTotalLength() {
        return mTotalContentWidth;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawRect(mMiddlePos - mMiddleLineWidth / 2, 0, mMiddlePos + mMiddleLineWidth / 2, getHeight(), mPaint);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public interface IAdapter<T> {
        View onCreateView(LayoutInflater inflater, ViewGroup parent);

        void onBindView(View view, T t);

        int getItemCount();

        T getItem(int position);
    }

    public abstract static class Adapter<TH> implements IAdapter<TH> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        public Adapter() {
            mDatas = new ArrayList<>();
        }

        protected List<TH> mDatas;

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void updateDatas(List<TH> datas) {
            setDatas(datas);
            notifyDataChange();
        }

        public void setDatas(List<TH> datas) {
            mDatas.clear();
            mDatas.addAll(datas);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public void notifyDataChange() {
            mObservable.notifyChanged();
        }

        @Override
        public TH getItem(int position) {
            return mDatas.get(position);
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public static abstract class AdapterDataObserver {
        public void onChanged() {

        }
    }

    private class ChildViewDataObserver extends AdapterDataObserver {
        ChildViewDataObserver() {
        }

        @Override
        public void onChanged() {
            reBindView();
            requestLayout();
        }
    }

    public void setAdapter(Adapter adapter) {
        adapter.registerAdapterDataObserver(mObserver);
        mAdapter = adapter;
        reBindView();
        requestLayout();
    }

    private void reBindView() {
        removeAllViews();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            View view = mAdapter.onCreateView(LayoutInflater.from(getContext()), this);
            mAdapter.onBindView(view, mAdapter.getItem(i));
            addView(view);
        }
    }
}
