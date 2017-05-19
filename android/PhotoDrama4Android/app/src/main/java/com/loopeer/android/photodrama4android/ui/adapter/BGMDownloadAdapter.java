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
import com.loopeer.android.photodrama4android.databinding.ListItemBgmDownloadBinding;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.MusicInfoUtils;
import com.loopeer.bottomimagepicker.PickerFragment;
import com.loopeer.itemtouchhelperextension.Extension;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import zlc.season.rxdownload2.RxDownload;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getBgmFormatDurationFromLocal;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getFormatDuration;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getFormatDurationFromLocal;

public class BGMDownloadAdapter extends BaseFooterAdapter<Voice> {

    //当前播放的item
    private int mPlayingPosition = -1;


    private IMusicAdapter mIMusicAdapter;

    public void setIMusicAdapter(IMusicAdapter iMusicAdapter) {
        this.mIMusicAdapter = iMusicAdapter;
    }

    public interface IMusicAdapter {
        void onMusicAddClick(Voice voice);
        void onControllerVisibilityChange(TextView txtStart, TextView txtCur, TextView txtEnd);
        void onMusicPlayClick(String path, MusicClipView musicClipView);
        void onMusicPauseClick(String path, MusicClipView musicClipView);
    }

    public BGMDownloadAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemBgmDownloadBinding binding =
            ((DataBindingViewHolder<ListItemBgmDownloadBinding>) holder).binding;

        binding.btnDelete.setOnClickListener(v -> doDelete(holder.getAdapterPosition()));

        binding.setVoice(voice);

        binding.txtStart.setText(getDefaultStartTime());
        binding.txtCur.setText(getDefaultStartTime());
        binding.txtEnd.setText(getBgmFormatDurationFromLocal(getContext(), voice));

        binding.layoutBrief.setOnClickListener(v -> {
            if (mPlayingPosition == pos) {
                mPlayingPosition = -1;
            } else {
                mPlayingPosition = pos;
            }
            notifyDataSetChanged();
        });

        final String path = FileManager.getInstance().getAudioBgmPath(getContext(), voice);
        //change ui
        if (mPlayingPosition == pos) { //play
            binding.layoutController.setVisibility(View.VISIBLE);
            binding.viewSwitcher.setDisplayedChild(1);
            binding.btnPausePlayBtn.setSelected(false);
            binding.btnExpand.setSelected(true);
            if (mIMusicAdapter != null) {
                mIMusicAdapter.onControllerVisibilityChange(binding.txtStart, binding.txtCur,
                    binding.txtEnd);
                mIMusicAdapter.onMusicPlayClick(path, binding.viewClip);
            }
        } else {//pause
            binding.viewSwitcher.setDisplayedChild(0);
            binding.layoutController.setVisibility(View.GONE);
            binding.btnExpand.setSelected(false);
            if (mIMusicAdapter != null) {
                // mIMusicAdapter.onControllerVisibilityChange(binding.txtStart, binding.txtCur,
                //     binding.txtEnd);
                mIMusicAdapter.onMusicPauseClick(path, binding.viewClip);
            }
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
        View v = getLayoutInflater().inflate(R.layout.list_item_bgm_download, parent, false);
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
