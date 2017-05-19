package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemEffectDownloadBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.itemtouchhelperextension.Extension;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getEffectFormatDurationFromLocal;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getFormatDurationFromLocal;

public class EffectDownloadAdapter extends BaseFooterAdapter<Voice> {

    public EffectDownloadAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemEffectDownloadBinding binding =
            ((DataBindingViewHolder<ListItemEffectDownloadBinding>) holder).binding;

        binding.btnDelete.setOnClickListener(v -> doDelete(holder.getAdapterPosition()));

        binding.txtStart.setText(getDefaultStartTime());
        binding.txtCur.setText(getDefaultStartTime());
        binding.txtEnd.setText(getEffectFormatDurationFromLocal(getContext(), voice));

        binding.setVoice(voice);
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_effect_download, parent, false);
        return new BGMDownloadAdapter.MusicItemViewHolder(v);
    }

    private void doDelete(int adapterPosition) {
        getDatas().remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        //TODO
    }


    public static class MusicItemViewHolder extends DataBindingViewHolder implements Extension {

        public MusicItemViewHolder(View itemView) {
            super(itemView);
        }

        public View getContentView() {
            return itemView.findViewById(R.id.layout_brief);
        }

        @Override
        public float getActionWidth() {
            return itemView.findViewById(R.id.btn_delete).getWidth();
        }
    }
}
