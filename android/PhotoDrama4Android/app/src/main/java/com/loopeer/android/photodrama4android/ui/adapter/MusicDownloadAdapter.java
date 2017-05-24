package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicDownloadBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicRecommendBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.File;

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

        String path = FileManager.getInstance().getAudioPath(getContext(), voice);

        binding.btnDownload.setOnClickListener(l -> {
            if (mListener != null) {
                mListener.onMusicDownloadClick(voice, binding.txtPercent);
                binding.viewSwitcher.setDisplayedChild(1);
            }
        });

        if (TextUtils.isEmpty(path)) {
            //未下载
            binding.viewSwitcher.setDisplayedChild(0);
            binding.txtPercent.setText(null);
            binding.txtPercent.setTextColor(
                getContext().getResources().getColor(R.color.colorAccent));
        } else {
            //已下载
            binding.viewSwitcher.setDisplayedChild(1);
            binding.txtPercent.setText(R.string.music_already_download);
            binding.txtPercent.setTextColor(
                getContext().getResources().getColor(R.color.text_color_tertiary));
        }

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
