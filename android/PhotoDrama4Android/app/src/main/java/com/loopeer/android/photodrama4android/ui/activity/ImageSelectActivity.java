package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.ui.widget.GalleryLinearLayout;
import com.loopeer.bottomimagepicker.BottomImagePickerView;
import com.loopeer.bottomimagepicker.ImageAdapter;
import com.loopeer.bottomimagepicker.PickerBottomBehavior;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectActivity extends MovieMakerBaseActivity {

    private BottomImagePickerView mBottomImagePickerView;
    private static GalleryLinearLayout mGalleryLinearLayout;
    private LinearLayout mLayoutDisplay;
    private static SimpleDraweeView mImageDisplay;

    private static ImageAdapter.OnImagePickListener mPickListener
        = new ImageAdapter.OnImagePickListener() {
        @Override public boolean onImagePick(Uri uri) {
            mGalleryLinearLayout.setUri(uri);
            mImageDisplay.setImageURI(uri);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        setupView();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_group_submit, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.action_submit) {
                View v = item.getActionView();
                if (v != null) {
                    v.setOnClickListener(v1 -> {
                        List<String> urls = new ArrayList<>();
                        Uri[] uris = mGalleryLinearLayout.getUris();
                        for (Uri uri : uris) {
                            if(uri != null) {
                                String path = uri.getPath();
                                urls.add(path);
                            }
                        }
                        // TODO: 2017/3/30 check urls.size
                        Navigator.startMakeMovieActivity(ImageSelectActivity.this,
                            Drama.createFromPath(urls));
                    });
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void setupView() {
        mBottomImagePickerView = (BottomImagePickerView) findViewById(R.id.pick_view);
        mGalleryLinearLayout = (GalleryLinearLayout) findViewById(R.id.layout_gallery);
        mLayoutDisplay = (LinearLayout) findViewById(R.id.layout_display);
        mImageDisplay = (SimpleDraweeView) findViewById(R.id.image_display);
        PickerBottomBehavior behavior = PickerBottomBehavior.from(mBottomImagePickerView);
        behavior.setBottomSheetCallback(new PickerBottomBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(
                @NonNull View bottomSheet, @PickerBottomBehavior.State int newState) {
                Log.e(PickerBottomBehavior.TAG, "newState = " + newState);
            }

            @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.e(PickerBottomBehavior.TAG, "onSlide");
            }
        });
        mBottomImagePickerView.post(() -> {
            final int peekHeight = mBottomImagePickerView.getPeekHeight();
            behavior.setPeekHeight(peekHeight);
        });
        mBottomImagePickerView.setOnImagePickListener(mPickListener);
        mGalleryLinearLayout.setOnGalleryItemClickListener(
            (position, uri) -> mImageDisplay.setImageURI(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mGalleryLinearLayout =null;
        mImageDisplay = null;
        mPickListener = null;
    }
}
