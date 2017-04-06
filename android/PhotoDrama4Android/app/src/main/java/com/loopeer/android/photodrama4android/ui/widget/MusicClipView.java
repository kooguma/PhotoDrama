package com.loopeer.android.photodrama4android.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.loopeer.android.photodrama4android.R;

public class MusicClipView extends View {

    private static final String TAG = "MusicClip";

    private static final int sDefaultMax = 100;

    private int mWidth;
    private int mHeight;
    private float mPosX;
    private float mPosY;

    private int mProgressStartX;
    private int mProgressStartY;

    private int mProgressMax;
    private int mProgressCur;

    private int mProgressWidth;
    private int mProgressColor;
    private int mProgressSelectedColor;
    private boolean mIsRoundCorner;
    private Paint mProgressPaint;

    private Indicator mIndicatorLeft;
    private Indicator mIndicatorRight;
    private Indicator mIndicatorTouched;

    private Paint mIndicatorLeftPaint;
    private Paint mIndicatorRightPaint;
    private Path mLeftTrianglePath;
    private Path mRightTrianglePath;

    private Paint mDotPaint;
    private int mDotColor;
    private int mDotProgress;
    private int mDotX;
    private int mDotY;

    public MusicClipView(Context context) {
        super(context);
    }

    public MusicClipView(Context context,
                         @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs, 0);
    }

    public MusicClipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Resources res = getResources();
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MusicClipView,
            defStyleAttr, 0);
        mProgressColor = a.getColor(R.styleable.MusicClipView_progressbarColor,
            res.getColor(R.color.music_clip_progress_color));
        mProgressSelectedColor = a.getColor(R.styleable.MusicClipView_progressbarSelectedColor,
            res.getColor(R.color.music_clip_progress_selected_color));
        mDotColor = a.getColor(R.styleable.MusicClipView_dotColor,
            res.getColor(R.color.music_clip_progress_dot_color));

        mProgressMax = a.getInteger(R.styleable.MusicClipView_progressMax, sDefaultMax);
        mIsRoundCorner = a.getBoolean(R.styleable.MusicClipView_roundCorner, false);

        a.recycle();

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setStrokeWidth(10);
        if (mIsRoundCorner) {
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        mIndicatorLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorLeftPaint.setColor(Color.BLUE);
        mIndicatorLeftPaint.setStrokeWidth(2);

        mIndicatorRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorRightPaint.setColor(Color.GREEN);
        mIndicatorRightPaint.setStrokeWidth(2);

        mIndicatorLeft = new Indicator();
        mIndicatorLeft.setRadius(15).setPaint(mIndicatorLeftPaint);
        mLeftTrianglePath = new Path();

        mIndicatorRight = new Indicator();
        mIndicatorRight.setRadius(15).setPaint(mIndicatorRightPaint);
        mRightTrianglePath = new Path();

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setColor(mDotColor);
        mDotPaint.setStrokeWidth(10);

    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();

        mProgressWidth = mWidth - getPaddingLeft() - getPaddingRight() - mIndicatorLeft.radius * 2;
        mProgressStartX = getPaddingLeft() + mIndicatorLeft.radius;
        mProgressStartY = mHeight / 2;

        final int cxl = mProgressStartX + mIndicatorLeft.cx;
        final int cyl = mProgressStartY + mIndicatorLeft.radius * 3;
        mIndicatorLeft.setPivotX(cxl).setPivotY(cyl);

        final int cxr = mProgressStartX + mProgressWidth;
        final int cyr = mProgressStartY + mIndicatorRight.radius * 3;
        mIndicatorRight.setPivotX(cxr).setPivotY(cyr);

    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        mPosX = event.getX();
        mPosY = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // TODO: 2017/3/31 重合的情况
                if (mIndicatorLeft.getRectF().contains(mPosX, mPosY)) {
                    //触摸左指示器
                    mIndicatorTouched = mIndicatorLeft;
                } else if (mIndicatorRight.getRectF().contains(mPosX, mPosY)) {
                    //触摸右指示器
                    mIndicatorTouched = mIndicatorRight;
                } else {
                    //触摸其它位置
                    mIndicatorTouched = null;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final int cx = (int) (mPosX - mIndicatorLeft.radius - getPaddingLeft());
                final int cy = mProgressStartY + mIndicatorLeft.radius * 3;
                boolean shouldInvalidate = false;
                if (mIndicatorTouched != null) {
                    if (checkIndicatorPivotX(cx)) {
                        mIndicatorTouched.setPivotX(cx);
                        shouldInvalidate = true;
                    }
                    if (checkIndicatorPivotY(cy)) {
                        mIndicatorTouched.setPivotY(cy);
                        shouldInvalidate = true;
                    }
                }
                if (shouldInvalidate) {
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                mIndicatorTouched = null;
                break;
        }
        return true;
    }

    private boolean checkIndicatorPivotX(int cx) {
        if (mIndicatorTouched.cx == cx) { //无变化
            return false;
        } else {
            if (mIndicatorTouched == mIndicatorLeft) {
                if (cx < mProgressStartX) {     //左边界
                    return false;
                } else if (cx > mIndicatorRight.cx) { //右边界
                    return false;
                } else {
                    return true;
                }
            } else {
                if (cx > mProgressStartX + mProgressWidth) { //右边界
                    return false;
                } else if (cx < mIndicatorLeft.cx) {//左边界
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private boolean checkIndicatorPivotY(int cy) {
        return cy != mIndicatorTouched.cy;
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: 2017/3/30 test
        Rect rect = new Rect();
        getDrawingRect(rect);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        //canvas.drawRect(rect, paint);

        drawProgress(canvas);
        drawIndicator(canvas);
        drawDot(canvas);
    }

    private void drawDot(Canvas canvas) {
        // TODO: 2017/3/31
        //final float scale = (float) mDotProgress / mProgressMax;
        final float x = mProgressStartX + 0.5f * mProgressWidth;
        final float y = mProgressStartY;
        canvas.drawPoint(x, y, mDotPaint);
    }

    private void drawProgress(Canvas canvas) {
        //1
        mProgressPaint.setColor(mProgressSelectedColor);
        canvas.drawLine(mProgressStartX, mProgressStartY, mIndicatorLeft.cx, mProgressStartY,
            mProgressPaint);
        //2
        mProgressPaint.setColor(mProgressColor);
        canvas.drawLine(mIndicatorLeft.cx, mProgressStartY, mIndicatorRight.cx, mProgressStartY,
            mProgressPaint);
        //3
        mProgressPaint.setColor(mProgressSelectedColor);
        canvas.drawLine(mIndicatorRight.cx, mProgressStartY, mProgressStartX + mProgressWidth,
            mProgressStartY, mProgressPaint);
    }

    private void drawIndicator(Canvas canvas) {
        drawIndicatorLeft(canvas);
        drawIndicatorRight(canvas);
    }

    private void drawIndicatorLeft(Canvas canvas) {
        drawIndicator(canvas, mIndicatorLeft, mLeftTrianglePath);
    }

    private void drawIndicatorRight(Canvas canvas) {
        drawIndicator(canvas, mIndicatorRight, mRightTrianglePath);
    }

    private void drawIndicator(Canvas canvas, Indicator indicator, Path path) {
        path.rewind();
        path.moveTo(indicator.cx - indicator.radius, indicator.cy);
        path.lineTo(indicator.cx + indicator.radius, indicator.cy);
        path.lineTo(indicator.cx, indicator.cy - indicator.radius * 3);
        path.close();
        indicator.setPath(path);
        indicator.draw(canvas);
    }

    public void setDotProgress(int dotProgress) {
        mDotProgress = dotProgress;
        postInvalidate();
    }

    class Indicator {
        int radius;
        int cx;
        int cy;

        Path path;
        Paint paint;

        public Indicator() {
        }

        public Indicator(int radius, int cx, int cy, Paint paint) {
            this.radius = radius;
            this.cx = cx;
            this.cx = cy;
            this.paint = paint;
        }

        public void draw(Canvas canvas) {
            drawCircle(canvas);
            drawTriangle(canvas);
        }

        private void drawCircle(Canvas canvas) {
            canvas.drawCircle(cx, cy, radius, paint);
        }

        private void drawTriangle(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        public RectF getRectF() {
            return new RectF(cx - radius * 2, cy - 4 * radius, cx + radius * 2, cy + radius * 2);
        }

        public Indicator setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public Indicator setPivotX(int cx) {
            this.cx = cx;
            return this;
        }

        public Indicator setPivotY(int cy) {
            this.cy = cy;
            return this;
        }

        public Indicator setPaint(Paint paint) {
            this.paint = paint;
            return this;
        }

        public Indicator setPath(Path path) {
            this.path = path;
            return this;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Indicator indicator = (Indicator) o;

            if (radius != indicator.radius) return false;
            if (cx != indicator.cx) return false;
            if (cy != indicator.cy) return false;
            if (path != null ? !path.equals(indicator.path) : indicator.path != null) return false;
            return paint != null ? paint.equals(indicator.paint) : indicator.paint == null;

        }

        @Override public int hashCode() {
            int result = radius;
            result = 31 * result + cx;
            result = 31 * result + cy;
            result = 31 * result + (path != null ? path.hashCode() : 0);
            result = 31 * result + (paint != null ? paint.hashCode() : 0);
            return result;
        }
    }
}