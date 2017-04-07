package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemDramaEditSegmentBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemImageSegmentBinding;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;

import java.util.List;

public class EditDramaSegmentAdapter extends BaseFooterAdapter<ImageClip> {

    private ImageClip mSelectedImageClip;

    public EditDramaSegmentAdapter(Context context) {
        super(context);
    }

    @Override
    public void updateData(List<ImageClip> data) {
        if (data != null && !data.isEmpty())
            mSelectedImageClip = data.get(0);
        mOnSelectedListener.onImageSelected(mSelectedImageClip);
        super.updateData(data);
    }

    @Override
    public void bindItem(ImageClip imageClip, int position, RecyclerView.ViewHolder viewHolder) {
        ListItemDramaEditSegmentBinding binding = (ListItemDramaEditSegmentBinding) ((DataBindingViewHolder) viewHolder).binding;
        binding.img.setLocalUrl(imageClip.path);
        binding.getRoot().setSelected(mSelectedImageClip.equals(imageClip));
        binding.img.setClickable(false);
        binding.getRoot().setOnClickListener(v -> {
            mSelectedImageClip = imageClip;
            mOnSelectedListener.onImageSelected(mSelectedImageClip);
            notifyDataSetChanged();
        });
        binding.executePendingBindings();
    }

    @Override
    public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder<>(getLayoutInflater().inflate(R.layout.list_item_drama_edit_segment, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_item_drama_edit_segment;
    }

    public void onBitmapReady(String path) {
        for (int i = 0; i < getDatas().size(); i++) {
            if (getDatas().get(i).path.equals(path)) {
                notifyItemChanged(i);
            }
        }
    }

    public interface OnSelectedListener {
        void onImageSelected(ImageClip imageClip);
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }
}
