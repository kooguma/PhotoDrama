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

import com.laputapp.rx.RxBus;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.service.VoiceService;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicDownloadBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicRecommendBinding;
import com.loopeer.android.photodrama4android.event.MusicDownFailEvent;
import com.loopeer.android.photodrama4android.event.MusicDownLoadSuccessEvent;
import com.loopeer.android.photodrama4android.event.MusicDownProgressEvent;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.AudioFetchHelper;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.Toaster;

import java.io.File;
import java.sql.Time;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MusicDownloadAdapter extends BaseFooterAdapter<Voice> {

    private ListItemMusicDownloadBinding mCurBinding;
    private MusicClip.MusicType mType;
    private final CompositeDisposable mAllSubscription = new CompositeDisposable();
    private OnItemClickListener<Voice> mListener;

    public MusicDownloadAdapter(Context context, MusicClip.MusicType type) {
        super(context);
        mType = type;
    }

    public void setListener(OnItemClickListener<Voice> listener) {
        mListener = listener;
    }

    @Override
    public void bindItem(Voice voice, int position, RecyclerView.ViewHolder holder) {
        ListItemMusicDownloadBinding binding =
                ((DataBindingViewHolder<ListItemMusicDownloadBinding>) holder).binding;
        binding.setVoice(voice);

        String path = FileManager.getInstance().getAudioPath(getContext(), mType, voice);
        registerItemDownloadEventListener(voice, binding);
        binding.viewSwitcherDisplay.setDisplayedChild(0);

        binding.btnDownload.setOnClickListener(l -> {
            if (mListener != null) {
                if (!TextUtils.isEmpty(path)) {
                    mListener.onItemClick(voice);
                    showMusicBar(binding);
                } else {
                    if (mType == MusicClip.MusicType.BGM) {
                        Analyst.addMusicSoundtrackDownloadClic(voice.id);
                    } else {
                        Analyst.addEffectSoundEffectDownloadClic(voice.id);
                    }
                    AudioFetchHelper.getAudio(mType, voice);
                    binding.viewSwitcher.setDisplayedChild(1);
                    binding.txtPercent.setText(R.string.music_zero_percent);
                }
            }
        });

        if (TextUtils.isEmpty(path)) {
            binding.viewSwitcher.setDisplayedChild(0);
            binding.txtPercent.setText(null);
            binding.btnDownload.setImageResource(R.drawable.ic_music_clip_download);
            binding.txtPercent.setTextColor(
                    ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else {
            binding.viewSwitcher.setDisplayedChild(0);
            binding.txtPercent.setText(null);
            binding.btnDownload.setImageResource(R.drawable.ic_download_success);
            binding.btnDownload.setEnabled(false);
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

    private void registerItemDownloadEventListener(Voice voice, ListItemMusicDownloadBinding binding) {
        registerSubscription(
                RxBus.getDefault()
                        .toFlowable(MusicDownProgressEvent.class)
                        .filter(o -> o != null && o.voice.id == voice.id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(o -> {
                            binding.viewSwitcher.setDisplayedChild(1);
                            binding.txtPercent.setText(
                                    getContext().getString(R.string.common_percent_format, o.progress));
                        })
                        .subscribe()
        );
        registerSubscription(
                RxBus.getDefault()
                        .toFlowable(MusicDownFailEvent.class)
                        .filter(o -> o != null && o.voice.id == voice.id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(o -> {
                            binding.viewSwitcher.setDisplayedChild(0);
                            binding.txtPercent.setText(null);
                            binding.btnDownload.setImageResource(R.drawable.ic_music_clip_download);
                            Toaster.showToast(o.failMessage);
                        })
                        .subscribe()
        );
        registerSubscription(
                RxBus.getDefault()
                        .toFlowable(MusicDownLoadSuccessEvent.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(o -> o != null && o.voice.id == voice.id)
                        .doOnNext(o -> {
                            binding.viewSwitcher.setDisplayedChild(0);
                            binding.txtPercent.setText(null);
                            binding.btnDownload.setImageResource(R.drawable.ic_download_success);
                            binding.btnDownload.setEnabled(false);
                        })
                        .subscribe()
        );
    }

    private void showMusicBar(ListItemMusicDownloadBinding binding) {
        if (mCurBinding != null) {
            mCurBinding.viewSwitcherDisplay.setDisplayedChild(0);
        }
        binding.viewSwitcherDisplay.setDisplayedChild(1);
        mCurBinding = binding;
    }

    @Override
    public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_music_download, parent, false);
        return new DataBindingViewHolder<>(v);
    }

    protected void registerSubscription(Disposable disposable) {
        mAllSubscription.add(disposable);
    }

    protected void unregisterSubscription(Disposable disposable) {
        mAllSubscription.remove(disposable);
    }

    protected void clearSubscription() {
        mAllSubscription.clear();
    }

    public void onDestroy() {
        clearSubscription();
    }
}
