package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.databinding.ListItemBgmDownloadBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.fragment.MyDownloadMusicFragment;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.itemtouchhelperextension.Extension;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;

public class BGMDownloadAdapter extends BaseFooterAdapter<Voice> {

    //当前播放的item
    private ListItemBgmDownloadBinding mPlayingItem;

    private IMusicAdapter mIMusicAdapter;
    private MyDownloadMusicFragment.IMusicDeleteListener mIMusicDeleteListener;

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

        String path = FileManager.getInstance().getAudioBgmPath(getContext(), voice);

        binding.btnDelete.setOnClickListener(v -> doDelete(holder.getAdapterPosition(),voice));

        binding.setVoice(voice);

        binding.txtStart.setText(getDefaultStartTime());
        binding.txtCur.setText(getDefaultStartTime());
        binding.txtEnd.setText(voice.duration);

        binding.layoutBrief.setOnClickListener(v -> {
            Analyst.addMusicSoundtrackPlayClick(voice.id);
            onItemExpand(binding, path);
        });

        binding.btnExpand.setOnClickListener(v -> {
            if (v.isSelected()) {
                if (mIMusicAdapter != null) {
                    Analyst.addMusicSoundtrackAddClick(voice.id);
                    mIMusicAdapter.onMusicAddClick(voice);
                }
            } else {
                Analyst.addMusicSoundtrackDetailClick(voice.id);
                onItemExpand(binding, path);
            }
        });

        binding.btnPausePlayBtn.setOnClickListener(v -> {
            if (v.isSelected()) {
                if (mIMusicAdapter != null) {
                    Analyst.addMusicSoundtrackPlayClick(voice.id);
                    mIMusicAdapter.onMusicPlayClick(path, mPlayingItem.viewClip);
                    v.setSelected(false);
                }
            } else {
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onMusicPauseClick(path, mPlayingItem.viewClip);
                    v.setSelected(true);
                }
            }
        });

        binding.executePendingBindings();
    }

    private void doDelete(int adapterPosition,Voice voice) {
        FileManager.getInstance().deleteAudioBmgFile(getContext(),voice);
        mIMusicDeleteListener.onMusicDelete(voice);
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

    private void onItemExpand(ListItemBgmDownloadBinding binding, String path) {
        if (mPlayingItem == null) {
            //当前无播放item
            //更新
            mPlayingItem = binding;
            play(binding, path);
        } else {
            if (mPlayingItem == binding) {
                //相同播放item
                if (binding.layoutController.getVisibility() == View.GONE) {
                    //未播放，开始播放
                    play(binding, path);
                    //更新
                    mPlayingItem = binding;
                } else {
                    //已经播放，暂停播放
                    pause(binding, path);
                }
            } else {
                //不同播放item
                //暂停播放的
                pause(mPlayingItem, path);
                //播放选中的
                play(binding, path);
                //更新
                mPlayingItem = binding;
            }
        }
    }

    private void pause(ListItemBgmDownloadBinding binding, String path) {
        binding.viewSwitcher.setDisplayedChild(0);
        binding.layoutController.setVisibility(View.GONE);
        binding.btnExpand.setSelected(false);
        if (mIMusicAdapter != null) {
            mIMusicAdapter.onControllerVisibilityChange(binding.layoutController);
            mIMusicAdapter.onMusicPauseClick(path, binding.viewClip);
        }
    }

    private void play(ListItemBgmDownloadBinding binding, String path) {
        binding.layoutController.setVisibility(View.VISIBLE);
        binding.viewSwitcher.setDisplayedChild(1);
        binding.btnPausePlayBtn.setSelected(false);
        binding.btnExpand.setSelected(true);
        if (mIMusicAdapter != null) {
            mIMusicAdapter.onControllerVisibilityChange(binding.layoutController);
            mIMusicAdapter.onMusicPlayClick(path, binding.viewClip);
        }
    }

    public void setIMusicDeleteListener(MyDownloadMusicFragment.IMusicDeleteListener IMusicDeleteListener) {
        mIMusicDeleteListener = IMusicDeleteListener;
    }
}
