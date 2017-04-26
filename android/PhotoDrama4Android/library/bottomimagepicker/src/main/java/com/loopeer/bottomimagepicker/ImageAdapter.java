package com.loopeer.bottomimagepicker;

import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
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
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private List<Image> mImages;
    private int mImageSize;
    private OnImagePickListener mOnImagePickListener;
    //保存选中状态
    private SparseBooleanArray mSelectedArray;

    public interface OnImagePickListener extends Serializable {
        boolean onImagePick(Uri uri);
    }

    public void setOnImagePickListener(OnImagePickListener listener) {
        this.mOnImagePickListener = listener;
    }

    public void setImages(List<Image> images) {
        mImages = images;
        notifyDataSetChanged();
    }

    public ImageAdapter(List<Image> images, int imageSize) {
        mImages = images;
        mImageSize = imageSize;
        mSelectedArray = new SparseBooleanArray(9);
    }

    @Override public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.list_item_picker_image, parent, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mImageSize, mImageSize);
        itemView.setLayoutParams(params);
        return new ImageHolder(itemView);
    }

    @Override public void onBindViewHolder(ImageHolder holder, int position) {
        Image image = mImages.get(position);
        holder.mImageView.setSelected(mSelectedArray.get(position));
        holder.bind(image);
    }

    @Override public int getItemCount() {
        return mImages == null ? 0 : mImages.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView mImageView;

        public ImageHolder(View itemView) {
            super(itemView);
            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.picker_image);
        }

        public void bind(Image image) {
            final Uri uri = Uri.fromFile(new File(image.url));

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(mImageSize, mImageSize))
                .setLocalThumbnailPreviewsEnabled(true)
                .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mImageView.getController())
                .build();
            mImageView.setController(controller);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (mOnImagePickListener != null) {
                        if (mOnImagePickListener.onImagePick(uri)) {
                            mSelectedArray.put(getLayoutPosition(), true);
                            notifyItemChanged(getLayoutPosition());
                        }
                    }
                }
            });
        }
    }
}
