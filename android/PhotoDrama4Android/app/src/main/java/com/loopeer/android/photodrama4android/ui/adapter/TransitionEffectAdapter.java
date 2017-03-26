package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemTransitionEffectBinding;
import com.loopeer.android.photodrama4android.opengl.model.TransitionClip;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;

public class TransitionEffectAdapter extends BaseFooterAdapter<TransitionClip> {

    private TransitionClip mSelectedTransitionClip;

    public TransitionEffectAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindItem(TransitionClip transitionClip, int position, RecyclerView.ViewHolder viewHolder) {
        ListItemTransitionEffectBinding binding = (ListItemTransitionEffectBinding) ((DataBindingViewHolder) viewHolder).binding;
        binding.getRoot().setSelected(mSelectedTransitionClip.equals(transitionClip));
        binding.textTitle.setText(getContext().getString(transitionClip.transitionType.getName()));
        binding.imgIcon.setClickable(false);
        binding.imgIcon.setImageResource(transitionClip.transitionType.getIcon());
        binding.getRoot().setOnClickListener(v -> {
            selectedItem(transitionClip);
        });
        binding.executePendingBindings();
    }

    public void selectedItem(TransitionClip transitionClip) {
        int index = getDatas().indexOf(transitionClip);
        if (index >= 0) {
            mSelectedTransitionClip = getDatas().get(index);
            mOnSelectedListener.onEffectSelected(mSelectedTransitionClip);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder<>(getLayoutInflater().inflate(R.layout.list_item_transition_effect, parent, false));
    }

    public interface OnSelectedListener {
        void onEffectSelected(TransitionClip TransitionClip);
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }
}
