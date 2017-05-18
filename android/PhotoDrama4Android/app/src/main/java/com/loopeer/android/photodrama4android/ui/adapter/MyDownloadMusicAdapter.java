package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.loopeer.itemtouchhelperextension.Extension;

import java.io.File;
import java.text.SimpleDateFormat;
import zlc.season.rxdownload2.RxDownload;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getFormatDuration;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getFormatDurationFromLocal;

public class MyDownloadMusicAdapter extends BaseFooterAdapter<Voice> {

    //当前播放的item
    private int mPlayingPosition = -1;

    private IMusicAdapter mIMusicAdapter;

    public class ItemState{
        boolean isPlaying;

    }


    public void setIMusicAdapter(IMusicAdapter iMusicAdapter) {
        this.mIMusicAdapter = iMusicAdapter;
    }

    public interface IMusicAdapter {
        void onMusicAddClick(Voice voice);
        void onControllerShow(TextView txtStart, TextView txtCur, TextView txtEnd);
        void onMusicPlayClick(String path, MusicClipView musicClipView);
        void onMusicPauseClick(String path, MusicClipView musicClipView);
    }

    public MyDownloadMusicAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemMusicSelectBinding binding =
            ((DataBindingViewHolder<ListItemMusicSelectBinding>) holder).binding;

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDelete(holder.getAdapterPosition());
            }
        });
        binding.setVoice(voice);

        binding.txtStart.setText(getDefaultStartTime());
        binding.txtCur.setText(getDefaultStartTime());
        binding.txtEnd.setText(getFormatDurationFromLocal(getContext(), voice));

        binding.layoutBrief.setOnClickListener(v -> {
            mPlayingPosition = pos;
            notifyDataSetChanged();
        });

        //change ui
        if (mPlayingPosition == pos) { //playing item
            final String path = FileManager.getInstance().getAudioPath(getContext(), voice);
            Log.e("tag", "controller  = " + binding.layoutController.getVisibility());
            if (binding.layoutController.getVisibility() == View.VISIBLE) {
                binding.layoutController.setVisibility(View.GONE);
                binding.viewSwitcher.setDisplayedChild(0);
                binding.btnPausePlayBtn.setSelected(true);
                binding.btnExpand.setSelected(false);
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onMusicPauseClick(path, binding.viewClip);
                }
            } else {
                binding.layoutController.setVisibility(View.VISIBLE);
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onControllerShow(binding.txtStart, binding.txtCur, binding.txtEnd);
                    mIMusicAdapter.onMusicPlayClick(path, binding.viewClip);
                }
                binding.viewSwitcher.setDisplayedChild(1);
                binding.btnPausePlayBtn.setSelected(false);
                binding.btnExpand.setSelected(true);
            }
        } else { // not playing item
            binding.viewSwitcher.setDisplayedChild(0);
            binding.layoutController.setVisibility(View.GONE);
            binding.btnExpand.setSelected(false);
        }

        binding.btnExpand.setOnClickListener(v -> {
            if (v.isSelected()) {

            }
        });

        binding.executePendingBindings();
    }

    private void doDelete(int adapterPosition) {
        getDatas().remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        //TODO
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_music_select, parent, false);
        return new MusicItemViewHolder(v);
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
