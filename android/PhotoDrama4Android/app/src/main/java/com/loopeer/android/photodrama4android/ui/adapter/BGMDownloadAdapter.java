package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemBgmDownloadBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.itemtouchhelperextension.Extension;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;

public class BGMDownloadAdapter extends BaseFooterAdapter<Voice> {

    //当前播放的item
    private ListItemBgmDownloadBinding mPlayingItem;

    private IMusicAdapter mIMusicAdapter;

    public void setIMusicAdapter(IMusicAdapter iMusicAdapter) {
        this.mIMusicAdapter = iMusicAdapter;
    }

    public interface IMusicAdapter {
        void onMusicAddClick(Voice voice);
        //void onControllerVisibilityChange(TextView txtStart, TextView txtCur, TextView txtEnd);
        void onControllerVisibilityChange(LinearLayout layoutController);
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
        binding.txtEnd.setText(voice.duration);

        binding.layoutBrief.setOnClickListener(v -> {
            String path = FileManager.getInstance().getAudioBgmPath(getContext(),voice);
            if (mPlayingItem == null) {
                //当前无播放item
                Log.e("tag", "0");
                //更新
                mPlayingItem = binding;
                play(binding,path);
            } else {
                if (mPlayingItem == binding) {
                    //相同播放item
                    Log.e("tag", "1");
                    if (binding.layoutController.getVisibility() == View.GONE) {
                        //未播放，开始播放
                        Log.e("tag", "1.1");
                        play(binding,path);
                        //更新
                        mPlayingItem = binding;
                    } else {
                        Log.e("tag", "1.2");
                        //已经播放，暂停播放
                        pause(binding,path);
                    }
                } else {
                    //不同播放item
                    //暂停播放的
                    Log.e("tag", "2");
                    pause(mPlayingItem,path);
                    //播放选中的
                    play(binding,path);
                    //更新
                    mPlayingItem = binding;
                }
            }
        });

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

    public void pause(ListItemBgmDownloadBinding binding,String path) {
        binding.viewSwitcher.setDisplayedChild(0);
        binding.layoutController.setVisibility(View.GONE);
        binding.btnExpand.setSelected(false);
        if (mIMusicAdapter != null) {
            mIMusicAdapter.onControllerVisibilityChange(binding.layoutController);
            mIMusicAdapter.onMusicPauseClick(path, binding.viewClip);
        }
    }

    public void play(ListItemBgmDownloadBinding binding,String path) {
        binding.layoutController.setVisibility(View.VISIBLE);
        binding.viewSwitcher.setDisplayedChild(1);
        binding.btnPausePlayBtn.setSelected(false);
        binding.btnExpand.setSelected(true);
        if (mIMusicAdapter != null) {
            mIMusicAdapter.onControllerVisibilityChange(binding.layoutController);
            mIMusicAdapter.onMusicPlayClick(path, binding.viewClip);
        }
    }

}
