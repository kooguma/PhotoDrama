package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemEffectDownloadBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.itemtouchhelperextension.Extension;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getEffectFormatDurationFromLocal;
import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getFormatDurationFromLocal;

public class EffectDownloadAdapter extends BaseFooterAdapter<Voice> {

    //当前播放的item
    private int mPlayingPosition = -1;

    public EffectDownloadAdapter(Context context) {
        super(context);
    }


    public interface IMusicAdapter {
        void onMusicAddClick(Voice voice);
        void onControllerVisibilityChange(TextView txtStart, TextView txtCur, TextView txtEnd);
        void onMusicPlayClick(String path, AppCompatSeekBar seekBar);
        void onMusicPauseClick(String path, AppCompatSeekBar seekBar);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemEffectDownloadBinding binding =
            ((DataBindingViewHolder<ListItemEffectDownloadBinding>) holder).binding;

        binding.btnDelete.setOnClickListener(v -> doDelete(holder.getAdapterPosition()));

        binding.txtStart.setText(getDefaultStartTime());
        binding.txtCur.setText(getDefaultStartTime());
        binding.txtEnd.setText(voice.duration);

        binding.setVoice(voice);

        binding.layoutBrief.setOnClickListener(v -> {
            if (mPlayingPosition == pos) {
                mPlayingPosition = -1;
            } else {
                mPlayingPosition = pos;
            }
            // TODO: 2017/5/19 卡顿
            notifyDataSetChanged();
        });

        //change ui
        if (mPlayingPosition == pos) { //play
            binding.layoutController.setVisibility(View.VISIBLE);
            binding.viewSwitcher.setDisplayedChild(1);
            binding.btnPausePlayBtn.setSelected(false);
            binding.btnExpand.setSelected(true);
            // if (mIMusicAdapter != null) {
            //     mIMusicAdapter.onControllerVisibilityChange(binding.txtStart, binding.txtCur,
            //         binding.txtEnd);
            //     mIMusicAdapter.onMusicPlayClick(path, binding.viewClip);
            // }
        } else {//pause
            binding.viewSwitcher.setDisplayedChild(0);
            binding.layoutController.setVisibility(View.GONE);
            binding.btnExpand.setSelected(false);
            // if (mIMusicAdapter != null) {
            //     // mIMusicAdapter.onControllerVisibilityChange(binding.txtStart, binding.txtCur,
            //     //     binding.txtEnd);
            //     mIMusicAdapter.onMusicPauseClick(path, binding.viewClip);
            // }
        }

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
