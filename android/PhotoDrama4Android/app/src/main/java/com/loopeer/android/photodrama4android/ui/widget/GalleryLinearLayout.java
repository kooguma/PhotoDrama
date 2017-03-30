package com.loopeer.android.photodrama4android.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.photodrama4android.R;
import java.security.PublicKey;

public class GalleryLinearLayout extends LinearLayout {

    private static final int sDefaultMaxCount = 9;

    private Uri[] mUris = new Uri[sDefaultMaxCount];
    private int mCurrentPos = -1;
    private OnGalleryItemClickListener mGalleryItemClickListener;

    public interface OnGalleryItemClickListener {
        void onGalleryItemClick(int position, Uri uri);
    }

    public void setOnGalleryItemClickListener(OnGalleryItemClickListener listener) {
        this.mGalleryItemClickListener = listener;
    }

    public GalleryLinearLayout(Context context) {
        super(context);
    }

    public GalleryLinearLayout(Context context,
                               @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryLinearLayout(Context context,
                               @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addSimpleDraweeViews();
    }

    private void addSimpleDraweeViews() {
        for (int i = 0; i < sDefaultMaxCount; i++) {
            addView(createSimpleDraweeView());
        }
    }

    private SimpleDraweeView createSimpleDraweeView() {
        SimpleDraweeView v = (SimpleDraweeView) LayoutInflater
            .from(getContext())
            .inflate(R.layout.view_galler_image, this, false);
        v.setOnClickListener(v1 -> {
            mCurrentPos = setSelected(v1);
            if (mGalleryItemClickListener != null) {
                mGalleryItemClickListener.onGalleryItemClick(mCurrentPos,getCurrentUri());
            }
        });
        return v;
    }

    private int setSelected(View v) {
        int index = -1;
        for (int i = 0; i < sDefaultMaxCount; i++) {
            final SimpleDraweeView child = (SimpleDraweeView) getChildAt(i);
            mCurrentPos = i;
            if (v.equals(child)) {
                index = i;
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
        return index;
    }

    public int getCurrentPos() {
        return mCurrentPos;
    }

    public void setUris(Uri[] uris) {
        mUris = uris;
        notifyDataChange();
    }

    public void setUri(int position, Uri uri) {
        if (!checkPositionValid(position)) return;
        mUris[position] = uri;
        notifyItemChange(position);
    }

    public void setUri(Uri uri) {
        final int position = mCurrentPos != -1 ? mCurrentPos : findFirstEmptyPosition();
        mUris[position] = uri;
        notifyItemChange(position);
    }

    public Uri getCurrentUri() {
        return getUri(mCurrentPos);
    }

    public Uri getUri(int position) {
        return !checkPositionValid(position) ? null : mUris[position];
    }

    public Uri[] getUris() {
        return mUris;
    }

    public void notifyDataChange() {
        for (int i = 0; i < sDefaultMaxCount; i++) {
            notifyItemChange(i);
        }
    }

    private void notifyItemChange(int position) {
        SimpleDraweeView v = (SimpleDraweeView) getChildAt(position);
        v.setImageURI(mUris[position]);
    }

    private int findFirstEmptyPosition() {
        for (int i = 0; i < sDefaultMaxCount; i++) {
            if (mUris[i] == null) {
                return i;
            }
        }
        return sDefaultMaxCount - 1;
    }

    private boolean checkPositionValid(int position) {
        return !(position < 0 || position > (sDefaultMaxCount - 1));
    }

}
