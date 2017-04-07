package com.loopeer.android.photodrama4android.ui.activity;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.MovieMakerGLSurfaceView;
import com.loopeer.android.photodrama4android.ui.widget.GalleryLinearLayout;
import com.loopeer.bottomimagepicker.BottomImagePickerView;
import com.loopeer.bottomimagepicker.ImageAdapter;
import com.loopeer.bottomimagepicker.PickerBottomBehavior;

public class DramaEditActivity extends MovieMakerBaseActivity {

    private MovieMakerGLSurfaceView mMovieMakerGLSurfaceView;
    private ImageView mIcon;
    private BottomImagePickerView mBottomImagePickerView;

    private Matrix mRotateMatrix;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_edit);
        setupView();
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        setCenterTitle(R.string.label_drama_edit);
    }

    private void setupView() {
        //mMovieMakerGLSurfaceView = (MovieMakerGLSurfaceView) findViewById(R.id.gl_surface_view);
        mRotateMatrix = new Matrix();
        mBottomImagePickerView = (BottomImagePickerView) findViewById(R.id.pick_view);
        mIcon = mBottomImagePickerView.getIconView();

        PickerBottomBehavior behavior = PickerBottomBehavior.from(mBottomImagePickerView);
        behavior.setBottomSheetCallback(new PickerBottomBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(
                @NonNull View bottomSheet, @PickerBottomBehavior.State int newState) {
            }

            @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                final float degrees = slideOffset * 180;
                mIcon.setRotation(degrees);
            }
        });
        mBottomImagePickerView.post(() -> {
            final int peekHeight = mBottomImagePickerView.getPeekHeight();
            behavior.setPeekHeight(peekHeight);
        });
        mBottomImagePickerView.setOnImagePickListener(new ImageAdapter.OnImagePickListener() {
            @Override public boolean onImagePick(Uri uri) {
                return false;
            }
        });
    }
}
