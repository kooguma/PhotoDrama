package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemEffectDownloadBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.itemtouchhelperextension.Extension;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;

public class EffectDownloadAdapter extends BaseFooterAdapter<Voice> {

    //当前播放的item

    private ListItemEffectDownloadBinding mPlayingItem;

    private IMusicAdapter mIMusicAdapter;

    public EffectDownloadAdapter(Context context) {
        super(context);
    }

    public void setIMusicAdapter(IMusicAdapter iMusicAdapter) {
        this.mIMusicAdapter = iMusicAdapter;
    }

    public interface IMusicAdapter {
        void onMusicAddClick(Voice voice);
        void onControllerVisibilityChange(LinearLayout layoutController);
        void onMusicPlayClick(String path, AppCompatSeekBar seekBar);
        void onMusicPauseClick(String path, AppCompatSeekBar seekBar);
    }

    @Override public void bindItem(Voice voice, int pos, RecyclerView.ViewHolder holder) {
        ListItemEffectDownloadBinding binding =
            ((DataBindingViewHolder<ListItemEffectDownloadBinding>) holder).binding;

        binding.btnDelete.setOnClickListener(v -> doDelete(holder.getAdapterPosition()));

        String path = FileManager.getInstance().getAudioEffectPath(getContext(), voice);

        binding.txtStart.setText(getDefaultStartTime());
        binding.txtCur.setText(getDefaultStartTime());
        binding.txtEnd.setText(voice.duration);

        binding.setVoice(voice);

        binding.layoutBrief.setOnClickListener(v -> {
            if (mPlayingItem == null) {
                //当前无播放item
                Log.e("tag", "0");
                //更新
                mPlayingItem = binding;
                play(binding, path);
            } else {
                if (mPlayingItem == binding) {
                    //相同播放item
                    Log.e("tag", "1");
                    if (binding.layoutController.getVisibility() == View.GONE) {
                        //未播放，开始播放
                        Log.e("tag", "1.1");
                        play(binding, path);
                        //更新
                        mPlayingItem = binding;
                    } else {
                        Log.e("tag", "1.2");
                        //已经播放，暂停播放
                        pause(binding, path);
                    }
                } else {
                    //不同播放item
                    //暂停播放的
                    Log.e("tag", "2");
                    pause(mPlayingItem, path);
                    //播放选中的
                    play(binding, path);
                    //更新
                    mPlayingItem = binding;
                }
            }
        });

        binding.btnExpand.setOnClickListener(v -> {
            if (v.isSelected()) {
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onMusicAddClick(voice);
                }
            }
        });

        binding.btnExpand.setOnClickListener(v -> {
            if (v.isSelected()) {
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onMusicAddClick(voice);
                }
            }
        });

        binding.btnPausePlayBtn.setOnClickListener(v -> {
            if(v.isSelected()){
                if(mIMusicAdapter != null){
                    mIMusicAdapter.onMusicPlayClick(path,mPlayingItem.seekBar);
                    v.setSelected(false);
                }
            }else {
                if(mIMusicAdapter != null){
                    mIMusicAdapter.onMusicPauseClick(path,mPlayingItem.seekBar);
                    v.setSelected(true);
                }
            }
        });

        binding.executePendingBindings();
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

    public void pause(ListItemEffectDownloadBinding binding, String path) {
        binding.viewSwitcher.setDisplayedChild(0);
        binding.layoutController.setVisibility(View.GONE);
        binding.btnExpand.setSelected(false);
        if (mIMusicAdapter != null) {
            mIMusicAdapter.onControllerVisibilityChange(binding.layoutController);
            mIMusicAdapter.onMusicPauseClick(path, binding.seekBar);
        }
    }

    public void play(ListItemEffectDownloadBinding binding, String path) {
        binding.layoutController.setVisibility(View.VISIBLE);
        binding.viewSwitcher.setDisplayedChild(1);
        binding.btnPausePlayBtn.setSelected(false);
        binding.btnExpand.setSelected(true);
        if (mIMusicAdapter != null) {
            mIMusicAdapter.onControllerVisibilityChange(binding.layoutController);
            mIMusicAdapter.onMusicPlayClick(path, binding.seekBar);
        }
    }
}
