package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicDownloadBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicRecommendBinding;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.Toaster;
import java.io.File;
import java.sql.Time;

public class MusicDownloadAdapter extends BaseFooterAdapter<Voice> {

    private ListItemMusicDownloadBinding mCurBinding;

    private IMusicDownloadAdapter mListener;

    private MusicClip.MusicType mType;

    public interface IMusicDownloadAdapter extends OnItemClickListener<Voice> {
        void onMusicDownloadClick(Voice voice, ViewSwitcher switcher);
    }

    public MusicDownloadAdapter(Context context, IMusicDownloadAdapter listener, MusicClip.MusicType type) {
        super(context);
        mListener = listener;
        mType = type;
    }

    @Override public void bindItem(Voice voice, int position, RecyclerView.ViewHolder holder) {
        ListItemMusicDownloadBinding binding =
            ((DataBindingViewHolder<ListItemMusicDownloadBinding>) holder).binding;
        binding.setVoice(voice);

        String path = FileManager.getInstance().getAudioPath(getContext(), mType, voice);

        binding.viewSwitcherDisplay.setDisplayedChild(0);

        binding.btnDownload.setOnClickListener(l -> {
            if (mListener != null) {
                if (!TextUtils.isEmpty(path)) {
                    mListener.onItemClick(voice);
                    showMusicBar(binding);
                } else {
                    mListener.onMusicDownloadClick(voice, binding.viewSwitcher);
                    binding.viewSwitcher.setDisplayedChild(1);
                    binding.txtPercent.setText(R.string.music_zero_percent);
                }
            }

        });

        if (TextUtils.isEmpty(path)) {
            //未下载
            binding.viewSwitcher.setDisplayedChild(0);
            binding.txtPercent.setText(null);
            binding.btnDownload.setImageResource(R.drawable.ic_music_clip_download);
            binding.txtPercent.setTextColor(
                ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else {
            //已下载
            binding.viewSwitcher.setDisplayedChild(0);
            binding.txtPercent.setText(null);
            binding.btnDownload.setImageResource(R.drawable.ic_download_success);
        }

        binding.getRoot().setOnClickListener(l -> {
            if (mListener != null) {
                mListener.onItemClick(voice);
                if (mCurBinding != null) {
                    mCurBinding.viewSwitcherDisplay.setDisplayedChild(0);
                }
                if (!TextUtils.isEmpty(path)) {
                    showMusicBar(binding);
                }
            }
        });
    }

    private void showMusicBar(ListItemMusicDownloadBinding binding) {
        if (mCurBinding != null) {
            mCurBinding.viewSwitcherDisplay.setDisplayedChild(0);
        }
        binding.viewSwitcherDisplay.setDisplayedChild(1);
        mCurBinding = binding;
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_music_download, parent, false);
        return new DataBindingViewHolder<>(v);
    }
}
