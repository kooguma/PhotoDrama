package com.loopeer.bottomimagepicker;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;

public class PickerFragment extends Fragment {

    private static final String IMAGE_LIST = "image_list";
    private static final String IMAGE_LISTENER = "image_listener";

    public static final int IMAGE_SIZE_UNIT = 10;
    public static final int DECORATION_SIZE_UNIT = 1;
    public static final int IMAGE_COUNT = 5;
    public static final int DECORATION_COUNT = 6;

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private ImageAdapter.OnImagePickListener mOnImagePickListener;
    private List<Image> mImages;
    private int mUnit;
    private WindowManager mWindowManager;

    public static PickerFragment newInstance(List<Image> images, ImageAdapter.OnImagePickListener listener) {
        PickerFragment fragment = new PickerFragment();
        fragment.setImages(images);
        fragment.setOnImagePickListener(listener);
        return fragment;
    }

    public void setOnImagePickListener(ImageAdapter.OnImagePickListener listener) {
        this.mOnImagePickListener = listener;
    }

    public void setImages(List<Image> images) {
        mImages = images;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mUnit = getUnitSize(mWindowManager);
        mImageAdapter = new ImageAdapter(mImages, mUnit * IMAGE_SIZE_UNIT);
        mImageAdapter.setOnImagePickListener(mOnImagePickListener);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picker, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_picker);
        mRecyclerView.setPadding(13, 0, 13, 0);
        mRecyclerView.setLayoutManager(
            new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(
            new GridLayoutItemDecoration(getSpacing(mWindowManager)));
        //weak reference
        mRecyclerView.setAdapter(mImageAdapter);

    }

    @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mImages = savedInstanceState.getParcelableArrayList(IMAGE_LIST);
            mImageAdapter.setImages(mImages);
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(IMAGE_LIST, (ArrayList<? extends Parcelable>) mImages);
    }

    public static int getUnitSize(WindowManager wm) {
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        return screenWidth /
            (DECORATION_COUNT * DECORATION_SIZE_UNIT + IMAGE_COUNT * IMAGE_SIZE_UNIT);
    }

    private int getSpacing(WindowManager wm) {
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        final int gridCount = IMAGE_COUNT;
        final int gridSize = mUnit * IMAGE_SIZE_UNIT;
        final int totalSpacing = screenWidth - gridCount * gridSize;
        return totalSpacing / (gridCount * 2 + 2);
    }

}
