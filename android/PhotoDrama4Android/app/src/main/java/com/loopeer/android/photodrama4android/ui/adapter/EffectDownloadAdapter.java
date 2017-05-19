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

public class EffectDownloadAdapter extends BaseFooterAdapter<Voice> {

    public EffectDownloadAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemEffectDownloadBinding binding =
            ((DataBindingViewHolder<ListItemEffectDownloadBinding>) holder).binding;
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_effect_download, parent, false);
        return new BGMDownloadAdapter.MusicItemViewHolder(v);
    }
}
