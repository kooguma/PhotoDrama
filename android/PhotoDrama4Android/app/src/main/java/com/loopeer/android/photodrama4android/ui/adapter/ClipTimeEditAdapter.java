package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemClipTimeEditBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemImageSegmentBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemTransitionEffectNoNameBinding;
import com.loopeer.android.photodrama4android.media.model.ImageClip;
import com.loopeer.android.photodrama4android.media.model.TransitionClip;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;

import java.util.List;

public class ClipTimeEditAdapter extends BaseFooterAdapter<ImageClip> {

    private ImageClip mSelectedImageClip;

    public ClipTimeEditAdapter(Context context) {
        super(context);
    }

    public void selectedFirstTransition() {
        if (getDatas() != null && !getDatas().isEmpty())
            mSelectedImageClip = findFirstTransition(getDatas());
        mOnSelectedListener.onImageClipSelected(mSelectedImageClip);
    }

    private ImageClip findFirstTransition(List<ImageClip> data) {
        return data.get(0);
    }

    @Override
    public void bindItem(ImageClip imageClip, int position, RecyclerView.ViewHolder viewHolder) {
        if (((DataBindingViewHolder) viewHolder).binding instanceof ListItemClipTimeEditBinding) {
            ListItemClipTimeEditBinding binding = (ListItemClipTimeEditBinding) ((DataBindingViewHolder) viewHolder).binding;
            binding.img.setLocalUrl(imageClip.path);
            binding.getRoot().setSelected(mSelectedImageClip.equals(imageClip));
            binding.img.setClickable(false);
            binding.setClip(imageClip);
            binding.getRoot().setOnClickListener(v -> {
                mSelectedImageClip = imageClip;
                mOnSelectedListener.onImageClipSelected(mSelectedImageClip);
                notifyDataSetChanged();
            });
            binding.executePendingBindings();
        }
    }

    @Override
    public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder<>(getLayoutInflater().inflate(R.layout.list_item_clip_time_edit, parent, false));
    }

    public ImageClip getSelectedImageClip() {
        return mSelectedImageClip;
    }

    public interface OnSelectedListener {
        void onImageClipSelected(ImageClip imageClip);
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }
}
