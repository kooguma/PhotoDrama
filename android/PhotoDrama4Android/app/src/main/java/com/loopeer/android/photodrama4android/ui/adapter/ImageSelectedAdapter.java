package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemDramaEditSegmentBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemImageSelectedBinding;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.bottomimagepicker.PickerFragment;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectedAdapter extends BaseFooterAdapter<ImageClip> {

    private ImageClip mSelectedImageClip;
    private List<ImageClip> mImageClips = new ArrayList<>();
    private int mImageSize;
    private int mSpace;
    private int mVerticalMargin;

    public ImageSelectedAdapter(Context context) {
        super(context);
        mImageSize = PickerFragment.getImageSize(getContext());
        mSpace = PickerFragment.getImageSpacing(getContext());
        mVerticalMargin = getContext().getResources().getDimensionPixelSize(R.dimen.larger_padding);
    }

    @Override
    public void updateData(List<ImageClip> data) {
        ArrayList<ImageClip> imageClips = new ArrayList<>();
        imageClips.addAll(data);
        if (imageClips.size() < 9)
            imageClips.add(null);
        super.updateData(imageClips);
    }

    private void selectedItem(ImageClip imageClip) {
        mSelectedImageClip = imageClip;
        mOnSelectedListener.onImageSelected(mSelectedImageClip);
        notifyDataSetChanged();
    }

    @Override
    public void bindItem(ImageClip imageClip, int position, RecyclerView.ViewHolder viewHolder) {
        ListItemImageSelectedBinding binding = (ListItemImageSelectedBinding) ((DataBindingViewHolder) viewHolder).binding;
        if ((imageClip != null && binding.img.getLocalUrl() != imageClip.path) || imageClip == null)
            binding.img.setLocalUrl(imageClip == null ? null : imageClip.path);
        if (imageClip == null) {
            binding.getRoot().setSelected(mSelectedImageClip == null);
        } else {
            binding.getRoot().setSelected(imageClip.equals(mSelectedImageClip));
        }
        binding.img.setClickable(false);
        binding.getRoot().setOnClickListener(v ->
                selectedItem(imageClip));
        binding.executePendingBindings();
    }

    @Override
    public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_item_image_selected, parent, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mImageSize
                , mImageSize);
        params.rightMargin = mSpace;
        params.leftMargin = mSpace;
        params.topMargin = mVerticalMargin;
        params.bottomMargin = mVerticalMargin;
        view.setLayoutParams(params);
        return new DataBindingViewHolder<>(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_item_image_selected;
    }

    public void addUri(Uri uri) {
        String path = uri.getPath();
        ImageClip imageClip = new ImageClip(path, 0);
        if (mSelectedImageClip != null) {
            mSelectedImageClip.path = path;
        } else {
            if (mImageClips.size() >= 9) {
                mImageClips.get(mImageClips.size() - 1).path = imageClip.path;
            } else {
                mImageClips.add(imageClip);
            }
        }
        updateData(mImageClips);
        selectedItem(getDatas().get(getDatas().size() - 1));
    }

    public void init() {
        updateData(mImageClips);
    }

    public List<String> getUrls() {
        List<String> results= new ArrayList<>();
        for (ImageClip imageClip :
                mImageClips) {
            results.add(imageClip.path);
        }
        return results;
    }

    public interface OnSelectedListener {
        void onImageSelected(ImageClip imageClip);
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }
}
