package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicSelectBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.File;
import zlc.season.rxdownload2.RxDownload;

public class MusicClipAddAdapter extends BaseFooterAdapter<Voice> {

    private IMusicAdapter mIMusicAdapter;

    public void setIMusicAdapter(IMusicAdapter iMusicAdapter) {
        this.mIMusicAdapter = iMusicAdapter;
    }

    public interface IMusicAdapter {
        void onAddAudioClick(Voice voice);
        void onDownloadClick(Voice voice, View v);
        void onAudioPlayClick(String path, MusicClipView musicClipView);
        void onAudioPauseClick(String path, MusicClipView musicClipView);
    }

    public MusicClipAddAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemMusicSelectBinding binding =
            ((DataBindingViewHolder<ListItemMusicSelectBinding>) holder).binding;
        binding.setVoice(voice);

        if (isAudioExists(voice)) {
            binding.btnDownload.setText("已下载");
            binding.btnDownload.setOnClickListener(v -> {
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onAddAudioClick(voice);
                }
            });
        } else {
            binding.btnDownload.setOnClickListener(v -> {
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onDownloadClick(voice, v);
                }
            });
        }

        binding.btnPausePlayBtn.setSelected(true);
        binding.btnPausePlayBtn.setOnClickListener(v -> {
            final String path = FileManager.getAudioPath(getContext(), voice);
            if (mIMusicAdapter != null) {
                if (!v.isSelected()) {
                    mIMusicAdapter.onAudioPauseClick(path, binding.viewClip);
                    v.setSelected(true);
                } else {
                    mIMusicAdapter.onAudioPlayClick(path, binding.viewClip);
                    v.setSelected(false);
                }
            }
        });
        binding.executePendingBindings();
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_music_select, parent, false);
        return new DataBindingViewHolder<>(v);
    }

    private boolean isAudioExists(Voice voice) {
        File[] file = RxDownload.getInstance(getContext()).getRealFiles(voice.voiceUrl);
        return file != null;
    }

}
