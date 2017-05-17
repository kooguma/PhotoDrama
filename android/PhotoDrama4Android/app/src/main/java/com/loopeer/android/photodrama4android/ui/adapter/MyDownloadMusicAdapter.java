package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.dynamic.utils.ViewUtils;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.laputapp.utilities.Utilities;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicSelectBinding;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.File;
import java.text.SimpleDateFormat;
import zlc.season.rxdownload2.RxDownload;

public class MyDownloadMusicAdapter extends BaseFooterAdapter<Voice> {

    private int mPlayingPosition;

    private IMusicAdapter mIMusicAdapter;

    public void setIMusicAdapter(IMusicAdapter iMusicAdapter) {
        this.mIMusicAdapter = iMusicAdapter;
    }

    public interface IMusicAdapter {
        void onMusicAddClick(Voice voice);
        void onMusicPlayClick(String path, MusicClipView musicClipView);
        void onMusicPauseClick(String path, MusicClipView musicClipView);
    }

    public MyDownloadMusicAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemMusicSelectBinding binding =
            ((DataBindingViewHolder<ListItemMusicSelectBinding>) holder).binding;

        binding.setVoice(voice);

        // if (isAudioExists(voice)) {
        //     // binding.btnDownload.setText("已下载");
        //     binding.btnDownload.setOnClickListener(v -> {
        //         if (mIMusicAdapter != null) {
        //             mIMusicAdapter.onAddAudioClick(voice);
        //         }
        //     });
        // } else {
        //     binding.btnDownload.setOnClickListener(v -> {
        //         if (mIMusicAdapter != null) {
        //             mIMusicAdapter.onDownloadClick(voice, v);
        //         }
        //     });
        // }

        binding.layoutBrief.setOnClickListener(v -> {
            if (binding.layoutController.getVisibility() == View.GONE) {
                binding.layoutController.setVisibility(View.VISIBLE);
                v.setSelected(true);
            } else {
                binding.layoutController.setVisibility(View.GONE);
                v.setSelected(false);
            }
        });

        binding.btnExpand.setOnClickListener(v -> {
            if (v.isSelected()) {

            }
        });

        if (pos != mPlayingPosition) {
            binding.btnPausePlayBtn.setSelected(true);
        }

        binding.btnPausePlayBtn.setOnClickListener(v -> {
            final String path = FileManager.getInstance().getAudioPath(getContext(), voice);
            if (mIMusicAdapter != null) {
                if (!v.isSelected()) {
                    mIMusicAdapter.onMusicPauseClick(path, binding.viewClip);
                    v.setSelected(true);
                    binding.viewSwitcher.setDisplayedChild(0);
                } else {
                    mIMusicAdapter.onMusicPlayClick(path, binding.viewClip);
                    mPlayingPosition = pos;
                    v.setSelected(false);
                    binding.viewSwitcher.setDisplayedChild(1);
                }
                notifyDataSetChanged();
            }
        });
        binding.executePendingBindings();
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_music_select, parent, false);
        return new DataBindingViewHolder<>(v);
    }

    private boolean isAudioExists(Voice voice) {
        // if(voice == null) return false;
        // File[] file = RxDownload.getInstance(getContext()).getRealFiles(voice.voiceUrl);
        // return file != null;
        return false;
    }

}
