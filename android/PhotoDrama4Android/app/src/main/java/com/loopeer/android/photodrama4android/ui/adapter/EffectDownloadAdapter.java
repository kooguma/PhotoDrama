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
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.databinding.ListItemBgmDownloadBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemEffectDownloadBinding;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.fragment.MyDownloadMusicFragment;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.itemtouchhelperextension.Extension;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import static com.loopeer.android.photodrama4android.utils.MusicInfoUtils.getDefaultStartTime;

public class EffectDownloadAdapter extends BaseFooterAdapter<Voice> {

    //当前播放的item

    private ListItemEffectDownloadBinding mPlayingItem;

    private IMusicAdapter mIMusicAdapter;
    private MyDownloadMusicFragment.IMusicDeleteListener mIMusicDeleteListener;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public EffectDownloadAdapter(Context context) {
        super(context);
    }

    public void setIMusicAdapter(IMusicAdapter iMusicAdapter) {
        this.mIMusicAdapter = iMusicAdapter;
    }

    public void setIMusicDeleteListener(MyDownloadMusicFragment.IMusicDeleteListener IMusicDeleteListener) {
        mIMusicDeleteListener = IMusicDeleteListener;
    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
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

        binding.btnDelete.setOnClickListener(v -> doDelete(holder.getAdapterPosition(),voice));

        String path = FileManager.getInstance().getAudioEffectPath(getContext(), voice);

        binding.txtStart.setText(getDefaultStartTime());
        binding.txtCur.setText(getDefaultStartTime());
        binding.txtEnd.setText(voice.duration);

        binding.setVoice(voice);

        binding.layoutBrief.setOnClickListener(v -> {
            Analyst.addEffectSoundEffectPlayClick(voice.id);
            onItemExpand(binding, path);
        });

        binding.btnExpand.setOnClickListener(v -> {
            if (v.isSelected()) {
                if (mIMusicAdapter != null) {
                    Analyst.addEffectSoundEffectAddClick(voice.id);
                    mIMusicAdapter.onMusicAddClick(voice);
                }
            } else {
                Analyst.addEffectSoundEffectDetailClick(voice.id);
                onItemExpand(binding, path);
            }
        });

        binding.btnPausePlayBtn.setOnClickListener(v -> {
            if (v.isSelected()) {
                if (mIMusicAdapter != null) {
                    Analyst.addEffectSoundEffectPlayClick(voice.id);
                    mIMusicAdapter.onMusicPlayClick(path, mPlayingItem.seekBar);
                    v.setSelected(false);
                }
            } else {
                if (mIMusicAdapter != null) {
                    mIMusicAdapter.onMusicPauseClick(path, mPlayingItem.seekBar);
                    v.setSelected(true);
                } else {
                    onItemExpand(binding, path);
                }
            }
        });

        binding.executePendingBindings();
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_effect_download, parent, false);
        return new BGMDownloadAdapter.MusicItemViewHolder(v);
    }

    private void doDelete(int adapterPosition,Voice voice) {
        FileManager.getInstance().deleteAudioEffectFile(getContext(),voice);
        mIMusicDeleteListener.onMusicDelete(voice);
    }

    private void onItemExpand(ListItemEffectDownloadBinding binding, String path) {
        mItemTouchHelperExtension.closeOpened();
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
