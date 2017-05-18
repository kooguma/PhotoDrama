package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.model.SubtitleClip;
import com.loopeer.android.photodrama4android.media.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.model.SubtitleEditRectInfo;
import com.loopeer.android.photodrama4android.utils.ShapeUtils;

import retrofit2.http.PATCH;

import static com.loopeer.android.photodrama4android.media.utils.TextureHelper.LINE_MAX_TEXT_NUM;
import static com.loopeer.android.photodrama4android.media.utils.TextureHelper.TEXTMARGINBOTTOM;
import static com.loopeer.android.photodrama4android.media.utils.TextureHelper.TEXT_LINE_PADDING;
import static com.loopeer.android.photodrama4android.media.utils.TextureHelper.TEXT_MARGIN_HORIZONTAL;

public class SubtitleEditRectView extends View {
    protected float mRatioX;
    protected float mRatioY;

    private SubtitleClip mSubtitleClip;
    private SubtitleEditRectInfo mSubtitleEditRectInfo;
    private SubtitleRectClickListener mSubtitleRectClickListener;

    public SubtitleEditRectView(Context context) {
        this(context, null);
    }

    public SubtitleEditRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubtitleEditRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs, 0);

        setTouchListener();
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieMakerTextureView, defStyleAttr, 0);
        if (a == null) return;

        mRatioX = a.getFloat(R.styleable.MovieMakerTextureView_screenRatioX, 1f);
        mRatioY = a.getFloat(R.styleable.MovieMakerTextureView_screenRatioY, 1f);
        a.recycle();
    }

    public void setSubtitleRectClickListener(SubtitleRectClickListener subtitleRectClickListener) {
        mSubtitleRectClickListener = subtitleRectClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
            int childWidthSize = getMeasuredWidth();
            if (childWidthSize == Math.max(DeviceScreenUtils.getScreenHeight(getContext()), DeviceScreenUtils.getScreenWidth(getContext()))) {
                int childHeightSize = Math.min(DeviceScreenUtils.getScreenHeight(getContext())
                        , DeviceScreenUtils.getScreenWidth(getContext()));
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childHeightSize * mRatioX / mRatioY), MeasureSpec.EXACTLY);
            } else {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childWidthSize * mRatioY / mRatioX), MeasureSpec.EXACTLY);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void showTextRect(SubtitleClip subtitleClip) {
        mSubtitleClip = subtitleClip;
        mSubtitleEditRectInfo = new SubtitleEditRectInfo();
        invalidate();
    }

    public void hideTextRect() {
        mSubtitleEditRectInfo = null;
        mSubtitleClip = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSubtitleClip != null) {
            draw(getContext(), new SubtitleInfo(getWidth(), getHeight(), mSubtitleClip.content), canvas);
        }
    }

    private SubtitleInfo draw(Context context, SubtitleInfo subtitleInfo, Canvas canvas) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap deleteBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_subtitle_delete, options);
        Paint textPaint = new Paint();
        float textSize = 1f * subtitleInfo.width / LINE_MAX_TEXT_NUM;
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setShadowLayer(2f, 2f, 2f, ContextCompat.getColor(context, android.R.color.black));
        textPaint.setColor(ContextCompat.getColor(context, android.R.color.white));

        drawText(context, subtitleInfo, canvas, textPaint, deleteBitmap);
        return subtitleInfo;
    }

    private void drawText(Context context, SubtitleInfo subtitleInfo, Canvas canvas, Paint textPaint, Bitmap deleteBitmap) {
        float textWidth = textPaint.measureText(subtitleInfo.content);
        float left = 0, right = 0, top = 0, bottom = 0;
        float horizontalMargin = TEXT_MARGIN_HORIZONTAL * 2 / 5;
        float verticalMargin = TEXTMARGINBOTTOM / 2;

        if (textWidth + TEXT_MARGIN_HORIZONTAL * 2 > subtitleInfo.width) {
            float textSingleWidth = textPaint.measureText("我");
            int maxText = (int) ((subtitleInfo.width - TEXT_MARGIN_HORIZONTAL * 2) / textSingleWidth);
            int line = subtitleInfo.content.length() / maxText + (subtitleInfo.content.length() % maxText == 0 ? 0 : 1);

            for (int i = 0; i < line; i++) {
                String s;
                if (i == line - 1) {
                    s = subtitleInfo.content.substring(maxText * i, subtitleInfo.content.length());
                } else {
                    s = subtitleInfo.content.substring(maxText * i, maxText * (i + 1));
                }
                float drawTextWidth = textPaint.measureText(s);
                Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
                float height = fontMetrics.bottom - fontMetrics.ascent;
                float y = subtitleInfo.height - fontMetrics.descent - TEXTMARGINBOTTOM - (line - i - 1) * height - (line - i - 1) * TEXT_LINE_PADDING;
                float x = subtitleInfo.width / 2 - drawTextWidth / 2;
                if (i == 0) {
                    left = x - horizontalMargin;
                    top = y + fontMetrics.top - verticalMargin;
                    right = left + drawTextWidth + horizontalMargin * 2;
                }
                if (i == line - 1) {
                    bottom = y + fontMetrics.bottom + verticalMargin;
                }
            }

            if (deleteBitmap != null) {
                drawDeleteRect(context, canvas, left, right, top, bottom, deleteBitmap);
                mSubtitleEditRectInfo.updateRect(left, top, right, bottom);
                mSubtitleEditRectInfo.updateDeleteSize(deleteBitmap.getWidth(), deleteBitmap.getHeight());
            }
            return;
        }
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float y = subtitleInfo.height - fontMetrics.descent - TEXTMARGINBOTTOM;
        float x = subtitleInfo.width / 2 - textWidth / 2;

        left = x - horizontalMargin;
        top = y + fontMetrics.top - verticalMargin;
        right = left + textWidth + horizontalMargin * 2;
        bottom = y + fontMetrics.bottom + verticalMargin;
        if (deleteBitmap != null) {
            drawDeleteRect(context, canvas, left, right, top, bottom, deleteBitmap);
            mSubtitleEditRectInfo.updateRect(left, top, right, bottom);
            mSubtitleEditRectInfo.updateDeleteSize(deleteBitmap.getWidth(), deleteBitmap.getHeight());
        }
    }

    private void drawDeleteRect(Context context, Canvas canvas, float left, float right, float top, float bottom, Bitmap deleteBitmap) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setColor(ContextCompat.getColor(context, android.R.color.white));
        paint.setShadowLayer(4f, 0f, 0f, ContextCompat.getColor(context, R.color.shadow_color));

        canvas.drawPath(ShapeUtils.RoundedRect(left
                , top
                , right, bottom, 8f, 8f), paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(right, top, deleteBitmap.getWidth() / 2, paint);
        canvas.drawBitmap(deleteBitmap, null, new RectF(right - deleteBitmap.getWidth() / 2
                , top - deleteBitmap.getHeight() / 2
                , right + deleteBitmap.getWidth() / 2
                , top + deleteBitmap.getHeight() / 2), paint);
    }

    private void setTouchListener() {
        setOnTouchListener(new View.OnTouchListener() {
            private int CLICK_ACTION_THRESHHOLD = 200;
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();
                        if (isAClick(startX, endX, startY, endY)) {
                            doTextureViewClick(event);
                        }
                        break;
                }
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }

            private boolean isAClick(float startx, float endx, float starty, float endy) {
                float differenceX = Math.abs(startx - endx);
                float differenceY = Math.abs(starty - endy);
                if (differenceX > CLICK_ACTION_THRESHHOLD || differenceY > CLICK_ACTION_THRESHHOLD) {
                    return false;
                }
                return true;
            }
        });
    }

    private void doTextureViewClick(MotionEvent e) {
        final int pointerIndex = MotionEventCompat.getActionIndex(e);
        final float x = e.getX(pointerIndex);
        final float y = e.getY(pointerIndex);

        if (mSubtitleEditRectInfo != null) {
            if (checkTouchText(x, y, mSubtitleEditRectInfo)) {
                mSubtitleRectClickListener.onRectTextClick();
                return;
            }
            if (checkTouchDeleteBtn(x, y, mSubtitleEditRectInfo)) {
                mSubtitleRectClickListener.onRectDeleteClick();
                return;
            }
        }
        mSubtitleRectClickListener.onRectSpaceClick();
    }

    private boolean checkTouchText(float x, float y, SubtitleEditRectInfo subtitleInfo) {
        if (x > subtitleInfo.left && x < subtitleInfo.right
                && y > subtitleInfo.top && y < subtitleInfo.bottom) {
            return true;
        }
        return false;
    }

    private boolean checkTouchDeleteBtn(float x, float y, SubtitleEditRectInfo subtitleInfo) {
        if (x > subtitleInfo.right - subtitleInfo.deleteBtnWidth && x < subtitleInfo.right + subtitleInfo.deleteBtnWidth
                && y > subtitleInfo.top - subtitleInfo.deleteBtnHeight && y < subtitleInfo.top + subtitleInfo.deleteBtnHeight) {
            return true;
        }
        return false;
    }

    public interface SubtitleRectClickListener{
        void onRectTextClick();
        void onRectDeleteClick();
        void onRectSpaceClick();
    }
}
