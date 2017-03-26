package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemImageSegmentBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemTransitionEffectNoNameBinding;
import com.loopeer.android.photodrama4android.opengl.model.TransitionClip;
import com.loopeer.android.photodrama4android.opengl.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;

import java.util.List;

public class ImageTransitionSegmentAdapter extends BaseFooterAdapter<TransitionImageWrapper> {

    private TransitionImageWrapper mSelectedTransitionImageWrapper;

    public ImageTransitionSegmentAdapter(Context context) {
        super(context);
    }

    public void selectedFirstTransition() {
        if (getDatas() != null && !getDatas().isEmpty())
            mSelectedTransitionImageWrapper = findFirstTransition(getDatas());
        mOnSelectedListener.onImageTransitionSelected(mSelectedTransitionImageWrapper);
    }

    private TransitionImageWrapper findFirstTransition(List<TransitionImageWrapper> data) {
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).isImageClip()) {
                return data.get(i);
            }
        }
        return data.get(0);
    }

    @Override
    public void bindItem(TransitionImageWrapper transitionImageWrapper, int position, RecyclerView.ViewHolder viewHolder) {
        if (((DataBindingViewHolder) viewHolder).binding instanceof ListItemImageSegmentBinding) {
            ListItemImageSegmentBinding binding = (ListItemImageSegmentBinding) ((DataBindingViewHolder) viewHolder).binding;
            binding.img.setLocalUrl(transitionImageWrapper.imageClip.path);
            binding.img.setClickable(false);
            binding.executePendingBindings();
        }
        if (((DataBindingViewHolder) viewHolder).binding instanceof ListItemTransitionEffectNoNameBinding) {
            ListItemTransitionEffectNoNameBinding binding = (ListItemTransitionEffectNoNameBinding) ((DataBindingViewHolder) viewHolder).binding;
            binding.getRoot().setSelected(mSelectedTransitionImageWrapper.equals(transitionImageWrapper));
            binding.imgIcon.setClickable(false);
            binding.imgIcon.setImageResource(transitionImageWrapper.transitionClip.transitionType.getIcon());
            binding.getRoot().setOnClickListener(v -> {
                mSelectedTransitionImageWrapper = transitionImageWrapper;
                mOnSelectedListener.onImageTransitionSelected(mSelectedTransitionImageWrapper);
                notifyDataSetChanged();
            });
            binding.executePendingBindings();
        }
    }

    @Override
    public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.list_item_image_segment) {
            return new DataBindingViewHolder<>(getLayoutInflater().inflate(R.layout.list_item_image_segment, parent, false));
        }
        if (viewType == R.layout.list_item_transition_effect_no_name) {
            return new DataBindingViewHolder<>(getLayoutInflater().inflate(R.layout.list_item_transition_effect_no_name, parent, false));
        }
        return new DataBindingViewHolder<>(getLayoutInflater().inflate(R.layout.list_item_image_segment, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) != null && getItem(position).isImageClip()) {
            return R.layout.list_item_image_segment;
        } else if (getItem(position) != null) {
            return R.layout.list_item_transition_effect_no_name;
        }
        return super.getItemViewType(position);
    }

    public void notifyTransition(TransitionClip transitionClip) {
        mSelectedTransitionImageWrapper.transitionClip = transitionClip;
        notifyDataSetChanged();
    }

    public interface OnSelectedListener {
        void onImageTransitionSelected(TransitionImageWrapper TransitionImageWrapper);
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }
}
