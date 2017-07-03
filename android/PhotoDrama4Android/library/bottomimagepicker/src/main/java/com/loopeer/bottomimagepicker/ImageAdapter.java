package com.loopeer.bottomimagepicker;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_CAMERA = 10000;
    private static final int ITEM_IMAGE = 10001;

    private List<Image> mDatas;
    private ImageFolder mImageFolder;
    private int mImageSize;
    private OnImagePickListener mOnImagePickListener;
    private OnCameraClickListener mOnCameraClickListener;
    private int mSelectedPosition = -1;

    public void updateSelectedImage(String path) {
        Image image = new Image(path);
        mSelectedPosition = mDatas.indexOf(image);
        notifyDataSetChanged();
    }

    public interface OnImagePickListener extends Serializable {
        boolean onImagePick(String uri);
    }

    public interface OnCameraClickListener {
        boolean onCameraClick();
    }

    public void setOnImagePickListener(OnImagePickListener listener) {
        this.mOnImagePickListener = listener;
    }

    public void setOnCameraClickListener(OnCameraClickListener onCameraClickListener) {
        mOnCameraClickListener = onCameraClickListener;
    }

    public void updateDatas(List<Image> images) {
        List<Image> results = new ArrayList<>();
        if (mImageFolder.dir == null) {
            results.add(null);
        }
        results.addAll(images);
        setImages(results);
        notifyDataSetChanged();
    }

    private void setImages(List<Image> images) {
        mDatas = images;
    }

    public ImageAdapter(ImageFolder folder, int imageSize) {
        mImageFolder = folder;
        updateDatas(folder.images);
        mImageSize = imageSize;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view;
        switch (viewType) {
            case ITEM_CAMERA:
                view = inflater.inflate(R.layout.list_item_camera_peek, parent, false);
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(mImageSize, mImageSize);
                view.setLayoutParams(params1);
                return new CameraViewHolder(view);
            default:
                view = inflater.inflate(R.layout.list_item_picker_image, parent, false);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mImageSize, mImageSize);
                view.setLayoutParams(params);
                return new ImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageHolder) {
            ImageHolder imageHolder = (ImageHolder) holder;
            Image image = mDatas.get(position);
            imageHolder.mImageView.setSelected(mSelectedPosition == position);
            imageHolder.bind(image);
        }
        if (holder instanceof CameraViewHolder) {
            CameraViewHolder cameraViewHolder = (CameraViewHolder) holder;
            cameraViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCameraClickListener != null) mOnCameraClickListener.onCameraClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position) == null) {
            return ITEM_CAMERA;
        }
        return ITEM_IMAGE;
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView mImageView;

        public ImageHolder(View itemView) {
            super(itemView);
            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.picker_image);
        }

        public void bind(final Image image) {
            final Uri uri = Uri.fromFile(new File(image.url));

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(mImageSize / 3, mImageSize / 3))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(mImageView.getController())
                    .build();
            mImageView.setController(controller);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedImage(image.url, getLayoutPosition());
                }
            });
        }
    }

    public void selectedImage(String path, int layoutPosition) {
        if (mOnImagePickListener != null) {
            if (mOnImagePickListener.onImagePick(path)) {
                mSelectedPosition = layoutPosition;
                notifyDataSetChanged();
            }
        }
    }

    static class CameraViewHolder extends RecyclerView.ViewHolder {

        public CameraViewHolder(View itemView) {
            super(itemView);

        }
    }
}
