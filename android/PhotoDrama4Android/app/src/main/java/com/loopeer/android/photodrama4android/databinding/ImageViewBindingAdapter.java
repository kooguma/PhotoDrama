package com.loopeer.android.photodrama4android.databinding;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import com.facebook.drawee.view.SimpleDraweeView;

public class ImageViewBindingAdapter {
    @BindingAdapter(value = {"android:src", "android:tint", "android:tintMode"},
                    requireAll = false)
    public static void setImageWithTint(ImageView view, @DrawableRes
            int drawableRes, ColorStateList tint, PorterDuff.Mode tintMode) {
        setImageWithTint(view, getDrawable(view, drawableRes), tint, tintMode);
    }

    @BindingAdapter(value = {"android:src", "android:tint", "android:tintMode"},
                    requireAll = false)
    public static void setImageWithTint(ImageView view, Drawable drawable, ColorStateList tint, PorterDuff.Mode tintMode) {
        if (drawable != null) {
            if (tint != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTintList(drawable, tint);
                if (tintMode != null) {
                    DrawableCompat.setTintMode(drawable, tintMode);
                }
            }
            view.setImageDrawable(drawable);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (tint != null) {
                view.setImageTintList(tint);
            }
            if (tintMode != null) {
                view.setImageTintMode(tintMode);
            }
        }
    }

    private static Drawable getDrawable(View view, int drawableRes) {
        return drawableRes != 0 ? ContextCompat.getDrawable(view.getContext(), drawableRes) : null;
    }

    //SimpleDraweeView
    @BindingAdapter(value = {"imageUri"})
    public static void setImageUri(SimpleDraweeView view, String uri) {
        view.setImageURI(uri);
    }

}
