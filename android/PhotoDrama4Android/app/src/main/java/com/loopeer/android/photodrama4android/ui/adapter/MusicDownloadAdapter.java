package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicDownloadBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicRecommendBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;

public class MusicDownloadAdapter extends BaseFooterAdapter<Voice> {

    private IMusicDownloadAdapter mListener;

    public interface IMusicDownloadAdapter extends OnItemClickListener<Voice> {
        void onMusicDownloadClick(Voice voice, TextView txtProgress);
    }

    public MusicDownloadAdapter(Context context, IMusicDownloadAdapter listener) {
        super(context);
        mListener = listener;
    }

    @Override public void bindItem(Voice voice, int position, RecyclerView.ViewHolder holder) {
        ListItemMusicDownloadBinding binding =
            ((DataBindingViewHolder<ListItemMusicDownloadBinding>) holder).binding;
        binding.setVoice(voice);

        binding.btnDownload.setOnClickListener(l -> {
            if (mListener != null) {
                mListener.onMusicDownloadClick(voice, binding.txtPercent);
                binding.viewSwitcher.setDisplayedChild(1);
            }
        });
        binding.getRoot().setOnClickListener(l -> {
            if (mListener != null) {
                mListener.onItemClick(voice);
            }
        });
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_music_download, parent, false);
        return new DataBindingViewHolder<>(v);
    }
}
