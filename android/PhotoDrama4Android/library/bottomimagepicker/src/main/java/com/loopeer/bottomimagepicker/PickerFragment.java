package com.loopeer.bottomimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PickerFragment extends Fragment implements ImageAdapter.OnCameraClickListener {

    private static final String IMAGE_LIST = "image_list";
    private static final String IMAGE_SELECTED_POSITION = "image_selected_position";
    private static final String IMAGE_LISTENER = "image_listener";
    public static final int RESULT_TAKE_PHOTO = 2003;
    public static final String EXTRA_PHOTO_URL = "extra_photo_url";

    public static final int IMAGE_SIZE_UNIT = 10;
    public static final int DECORATION_SIZE_UNIT = 1;
    public static final int IMAGE_COUNT = 5;
    public static final int DECORATION_COUNT = 6;

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private ImageAdapter.OnImagePickListener mOnImagePickListener;
    private TabRefreshListener mTabRefreshListener;
    private String mPath;
    private int mUnit;
    private ImageFolder mImageFolder;
    private WindowManager mWindowManager;
    private List<Image> mImages;
    private int mTabIndex;

    public static PickerFragment newInstance(int position, ImageFolder folder
            , ImageAdapter.OnImagePickListener listener
            , TabRefreshListener tabRefreshListener) {
        PickerFragment fragment = new PickerFragment();
        fragment.mImageFolder = folder;
        fragment.mTabIndex = position;
        fragment.mImages = folder.images;
        fragment.mTabRefreshListener = tabRefreshListener;
        fragment.setOnImagePickListener(listener);
        return fragment;
    }

    public void setOnImagePickListener(ImageAdapter.OnImagePickListener listener) {
        this.mOnImagePickListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mUnit = getUnitSize(mWindowManager);
        mImageAdapter = new ImageAdapter(mImageFolder, mUnit * IMAGE_SIZE_UNIT);
        mImageAdapter.setOnImagePickListener(mOnImagePickListener);
        if (mImageFolder.dir == null) mImageAdapter.setOnCameraClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_picker);
        mRecyclerView.setPadding(13, 0, 13, 0);
        mRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(
                new GridLayoutItemDecoration(getSpacing(mWindowManager)));
        mRecyclerView.setAdapter(mImageAdapter);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mImages = savedInstanceState.getParcelableArrayList(IMAGE_LIST);
            mPath = savedInstanceState.getString(IMAGE_SELECTED_POSITION);
            mImageAdapter.updateDatas(mImages);
            mImageAdapter.updateSelectedImage(mPath);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(IMAGE_LIST, (ArrayList<? extends Parcelable>) mImages);
        outState.putString(IMAGE_SELECTED_POSITION, mPath);
    }

    public static int getUnitSize(WindowManager wm) {
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        return screenWidth /
                (DECORATION_COUNT * DECORATION_SIZE_UNIT + IMAGE_COUNT * IMAGE_SIZE_UNIT);
    }

    public static int getImageSize(Context context) {
        WindowManager wm = ((Activity) context).getWindowManager();
        int unitSize = getUnitSize(wm);
        return unitSize * IMAGE_SIZE_UNIT;
    }

    public static int getImageSpacing(Context context) {
        WindowManager wm = ((Activity) context).getWindowManager();
        int unitSize = getUnitSize(wm);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        final int gridCount = IMAGE_COUNT;
        final int gridSize = unitSize * IMAGE_SIZE_UNIT;
        final int totalSpacing = screenWidth - gridCount * gridSize;
        return totalSpacing / (gridCount * 2 + 2);
    }

    private int getSpacing(WindowManager wm) {
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        final int gridCount = IMAGE_COUNT;
        final int gridSize = mUnit * IMAGE_SIZE_UNIT;
        final int totalSpacing = screenWidth - gridCount * gridSize;
        return totalSpacing / (gridCount * 2 + 2);
    }

    public void updateSelectedImage(String path) {
        mImageAdapter.updateSelectedImage(path);
        this.mPath = path;
    }

    @Override
    public boolean onCameraClick() {
        if (CameraPermissionUtils.hasCameraPermission(getContext())) {
            startCamera();
        } else {
            CameraPermissionUtils.requestCameraPermissions(this);
        }
        return false;
    }

    private void startCamera() {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            startActivityForResult(new Intent(getContext(), UserCameraActivity.class), RESULT_TAKE_PHOTO,
                    null);
        } else {
            Toast.makeText(getContext(), "内存卡不存在", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK) return;

        String photoTakeUrl = data.getStringExtra(EXTRA_PHOTO_URL);
        if (requestCode == RESULT_TAKE_PHOTO && null != photoTakeUrl) {
            mImages.add(0, new Image(photoTakeUrl, "", System.currentTimeMillis()));
            mImageAdapter.updateDatas(mImages);
            mImageAdapter.selectedImage(photoTakeUrl, 1);
            if (mTabRefreshListener != null)
                mTabRefreshListener.onTabSelected(mTabIndex);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CameraPermissionUtils.REQUEST_CAMERA_PERMISSION && CameraPermissionUtils.hasCameraPermission(getContext())) {
            startCamera();
        }
    }

    public interface TabRefreshListener {
        void onTabSelected(int index);
    }
}
