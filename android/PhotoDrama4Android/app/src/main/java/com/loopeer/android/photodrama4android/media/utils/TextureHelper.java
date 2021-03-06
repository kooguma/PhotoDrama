/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.loopeer.android.photodrama4android.media.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;
import com.loopeer.android.librarys.imagegroupview.utils.ImageUtils;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.model.ImageInfo;
import com.loopeer.android.photodrama4android.media.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.utils.ShapeUtils;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {
    public static final float TEXT_MARGIN_BOTTOM = DeviceScreenUtils.dp2px(18, PhotoDramaApp.getAppContext());//TODO test value
    public static final float TEXT_LINE_PADDING = DeviceScreenUtils.dp2px(6, PhotoDramaApp.getAppContext());//TODO test value
    public static final float VERTICAL_MARGIN = DeviceScreenUtils.dp2px(6, PhotoDramaApp.getAppContext());//TODO test value
    public static final float TEXT_MARGIN_HORIZONTAL = DeviceScreenUtils.dp2px(40, PhotoDramaApp.getAppContext());//TODO test value
    public static final float TEXT_SIZE = dp2px(14, PhotoDramaApp.getAppContext());//TODO test value

    private static final String TAG = "TextureHelper";
    private static final boolean DEBUG = BuildConfig.DEBUG || true;



    private static int dp2px(float spValue, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (spValue * metrics.scaledDensity + 0.5f);
    }

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (BuildConfig.LOG_ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (BuildConfig.LOG_ON) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
        bitmap.recycle();
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

    public static ImageInfo loadTexture(Context context, String path) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (BuildConfig.LOG_ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object from path.");
            }
            return null;
        }

        long start = System.currentTimeMillis();
        final Bitmap bitmap = ImageUtils.imageZoomByScreen(context, path);
        long bittime = System.currentTimeMillis();
        if (BuildConfig.LOG_ON) {
            Log.w(TAG, "Paht " + path + "  load Time :  " + (bittime - start));
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (bitmap == null) {
            if (BuildConfig.LOG_ON) {
                Log.w(TAG, "Image path " + path + " could not be decoded.");
            }
            glDeleteTextures(1, textureObjectIds, 0);
            return null;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
        bitmap.recycle();
        glBindTexture(GL_TEXTURE_2D, 0);
        long bindtime = System.currentTimeMillis();
        if (BuildConfig.LOG_ON) {
            Log.w(TAG, "Paht " + path + "  bindtime :  " + (bindtime - bittime));
        }
        return new ImageInfo(textureObjectIds[0], width, height);
    }

    public static SubtitleInfo loadTexture(Context context, SubtitleInfo subtitleInfo) {

        Bitmap bitmap = Bitmap.createBitmap(subtitleInfo.width, subtitleInfo.height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);
        Paint textPaint = new Paint();
        float textSize = TEXT_SIZE;
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setShadowLayer(2f, 2f, 2f, ContextCompat.getColor(context, android.R.color.black));
        textPaint.setColor(ContextCompat.getColor(context, android.R.color.white));

        drawText(context, subtitleInfo, canvas, textPaint, null);

        int textureObjectIds[] = {0};
        glGenTextures(1, textureObjectIds, 0);
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, 0);
        bitmap.recycle();
        subtitleInfo.textureObjectId = textureObjectIds[0];
        return subtitleInfo;
    }

    public static SubtitleInfo loadSelectedTexture(Context context, SubtitleInfo subtitleInfo) {
        Bitmap bitmap = Bitmap.createBitmap(subtitleInfo.width, subtitleInfo.height, Bitmap.Config.ARGB_4444);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap deleteBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_subtitle_delete, options);

        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);
        Paint textPaint = new Paint();
        float textSize = TEXT_SIZE;
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setShadowLayer(2f, 2f, 2f, ContextCompat.getColor(context, android.R.color.black));
        textPaint.setColor(ContextCompat.getColor(context, android.R.color.white));

        drawText(context, subtitleInfo, canvas, textPaint, deleteBitmap);

        int textureObjectIds[] = {0};
        glGenTextures(1, textureObjectIds, 0);
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, 0);
        bitmap.recycle();
        subtitleInfo.textureObjectId = textureObjectIds[0];
        return subtitleInfo;
    }

    public static void drawText(Context context, SubtitleInfo subtitleInfo, Canvas canvas, Paint textPaint, Bitmap deleteBitmap) {
        float textWidth = textPaint.measureText(subtitleInfo.content);
        float left = 0, right = 0, top = 0, bottom = 0;
        float horizontalMargin = TEXT_MARGIN_HORIZONTAL * 3 / 8;
        float verticalMargin = VERTICAL_MARGIN;

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
                float y = subtitleInfo.height - fontMetrics.descent - TEXT_MARGIN_BOTTOM - (line - i - 1) * height - (line - i - 1) * TEXT_LINE_PADDING;
                float x = subtitleInfo.width / 2 - drawTextWidth / 2;
                canvas.drawText(s, x, y, textPaint);
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
            }
            return;
        }
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float y = subtitleInfo.height - fontMetrics.descent - TEXT_MARGIN_BOTTOM;
        float x = subtitleInfo.width / 2 - textWidth / 2;
        canvas.drawText(subtitleInfo.content, x, y, textPaint);

        left = x - horizontalMargin;
        top = y + fontMetrics.top - verticalMargin;
        right = left + textWidth + horizontalMargin * 2;
        bottom = y + fontMetrics.bottom + verticalMargin;
        if (deleteBitmap != null) {
            drawDeleteRect(context, canvas, left, right, top, bottom, deleteBitmap);
        }
    }

    public static void drawDeleteRect(Context context, Canvas canvas, float left, float right, float top, float bottom, Bitmap deleteBitmap) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setColor(ContextCompat.getColor(context, android.R.color.white));
        canvas.drawPath(ShapeUtils.RoundedRect(left
                , top
                , right, bottom, 8f, 8f), paint);

        canvas.drawBitmap(deleteBitmap, right - deleteBitmap.getWidth() / 2, top - deleteBitmap.getHeight() / 2, paint);
    }
}
