package com.loopeer.android.photodrama4android.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dynamic.UIAttr;
import com.dynamic.ViewBindHelper;
import com.dynamic.refresher.RefreshHelper;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.rx.RxBus;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.laputapp.ui.decorator.DividerItemDecoration;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.VoiceService;
import com.loopeer.android.photodrama4android.event.MusicDownLoadSuccessEvent;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.activity.AddMusicClipActivity;
import com.loopeer.android.photodrama4android.ui.adapter.BGMDownloadAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.EffectDownloadAdapter;
import com.loopeer.android.photodrama4android.ui.hepler.ItemTouchHelperCallback;
import com.loopeer.android.photodrama4android.ui.hepler.MediaPlayerWrapper;
import com.loopeer.android.photodrama4android.ui.widget.LocalSquareImageView;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import java.io.File;
import java.util.List;

public class MyDownloadMusicFragment extends MovieMakerBaseFragment
        implements IPageRecycler<Voice> {

    private MediaPlayerWrapper mPlayerWrapper;

    private MusicClip.MusicType mType;
    private ItemTouchHelperExtension mItemTouchHelperExtension;
    private BGMDownloadAdapter mBGMDownloadAdapter;
    private EffectDownloadAdapter mEffectDownloadAdapter;

    public static MyDownloadMusicFragment newInstance(MusicClip.MusicType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Navigator.EXTRA_MUSIC_CLIP, type);
        MyDownloadMusicFragment fragment = new MyDownloadMusicFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mType = (MusicClip.MusicType) getArguments().getSerializable(Navigator.EXTRA_MUSIC_CLIP);
        mPlayerWrapper = new MediaPlayerWrapper(getContext());
        super.onCreate(savedInstanceState);
        registerMusicDownLoadSuccessEvent();
        getRecyclerManager().setRefreshMode(RefreshHelper.RefreshMode.NONE);

    }

    private void registerMusicDownLoadSuccessEvent() {
        registerSubscription(
                RxBus.getDefault().toFlowable(MusicDownLoadSuccessEvent.class)
                        .doOnNext(o -> getRecyclerManager().onRefresh())
                        .doOnNext(o -> {
                            registerSubscription(
                                    VoiceService.INSTANCE
                                            .download(o.voice.id)
                                            .subscribe()
                            );
                        })
                        .subscribe()
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerManager().getRecyclerView()
                .addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.VERTICAL_LIST, 0,
                        DeviceScreenUtils.dp2px(0.5f, getContext())));
        getRecyclerManager().getLoadHelper().addViewBinder(
                UIAttr.LoaderAttr.LOOPEER_EMPTY, new ViewBindHelper() {
                    @Override
                    public View createView(LayoutInflater layoutInflater, ViewGroup viewParent) {
                        return layoutInflater.inflate(R.layout.view_empty_my_download, viewParent,
                                false);
                    }

                    @Override
                    public void viewCreate(View view) {
                        view.findViewById(R.id.btn_download).setOnClickListener(l
                                -> ((AddMusicClipActivity) getActivity()).switchToRecommend());
                    }
                });

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback();
        mItemTouchHelperExtension = new ItemTouchHelperExtension(callback);
        mItemTouchHelperExtension.attachToRecyclerView(getRecyclerManager().getRecyclerView());
        if (mBGMDownloadAdapter != null)
            mBGMDownloadAdapter.setItemTouchHelperExtension(mItemTouchHelperExtension);
        if (mEffectDownloadAdapter != null)
            mEffectDownloadAdapter.setItemTouchHelperExtension(mItemTouchHelperExtension);
    }

    @Override
    public int getExtraItemCount() {
        return 0;
    }

    @Override
    public RxRecyclerAdapter<Voice> createRecyclerViewAdapter() {
        if (mType == MusicClip.MusicType.BGM) {
            mBGMDownloadAdapter = new BGMDownloadAdapter(getContext());
            mBGMDownloadAdapter.setIMusicAdapter(new BGMDownloadAdapter.IMusicAdapter() {
                @Override
                public void onMusicAddClick(Voice voice) {
                    addMusic(voice);
                }

                @Override
                public void onControllerVisibilityChange(LinearLayout layoutController) {
                    updateController(layoutController);
                }

                @Override
                public void onMusicPlayClick(String path, MusicClipView musicClipView) {
                    playMusic(path, musicClipView);
                }

                @Override
                public void onMusicPauseClick(String path, MusicClipView musicClipView) {
                    pauseMusic(musicClipView);
                }
            });
            mBGMDownloadAdapter.setIMusicDeleteListener(voice -> getRecyclerManager().onRefresh());
            mBGMDownloadAdapter.setItemTouchHelperExtension(mItemTouchHelperExtension);
            return mBGMDownloadAdapter;
        } else {
            mEffectDownloadAdapter = new EffectDownloadAdapter(getContext());

            mEffectDownloadAdapter.setIMusicAdapter(new EffectDownloadAdapter.IMusicAdapter() {
                @Override
                public void onMusicAddClick(Voice voice) {
                    addMusic(voice);
                }

                @Override
                public void onControllerVisibilityChange(LinearLayout layoutController) {
                    updateController(layoutController);
                }

                @Override
                public void onMusicPlayClick(String path, AppCompatSeekBar seekBar) {
                    playMusic(path, seekBar);
                }

                @Override
                public void onMusicPauseClick(String path, AppCompatSeekBar seekBar) {
                    pauseMusic(seekBar);
                }
            });
            mEffectDownloadAdapter.setIMusicDeleteListener(voice -> getRecyclerManager().onRefresh());
            mEffectDownloadAdapter.setItemTouchHelperExtension(mItemTouchHelperExtension);
            return mEffectDownloadAdapter;
        }
    }

    @Override
    public Flowable<? extends BaseResponse<List<Voice>>> requestData(String offset, String page, String pageSize) {
        load();
        return null;
    }

    private void load() {
        List<Voice> voices = mType == MusicClip.MusicType.BGM
                ?
                FileManager.getInstance().getAudioBgmFiles()
                : FileManager.getInstance().getAudioEffectFiles();
        getRecyclerManager().onCacheLoaded(voices);
    }

    private void addMusic(Voice voice) {
        MusicClip clip = mPlayerWrapper.generateMusicClip(voice, mType);
        Intent intent = new Intent();
        intent.putExtra(Navigator.EXTRA_MUSIC_CLIP, clip);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private void updateController(LinearLayout layoutController) {
        mPlayerWrapper.updateController(layoutController);

    }

    private void playMusic(String path, MusicClipView musicClipView) {
        if (mPlayerWrapper.isAlreadyPrepared(new File(path))) {
            //当前item
            mPlayerWrapper.start();
        } else {
            if (mPlayerWrapper.isPlaying()) {
                mPlayerWrapper.reset();
            }
            mPlayerWrapper.updateMusicClipView(musicClipView);
            mPlayerWrapper.updateDataSource(new File(path));
            mPlayerWrapper.startAsync();
        }
    }

    private void playMusic(String path, AppCompatSeekBar seekBar) {
        if (mPlayerWrapper.isAlreadyPrepared(new File(path))) {
            //当前item
            mPlayerWrapper.start();
        } else {
            if (mPlayerWrapper.isPlaying()) {
                mPlayerWrapper.reset();
            }
            mPlayerWrapper.updateSeekBar(seekBar);
            mPlayerWrapper.updateDataSource(new File(path));
            mPlayerWrapper.startAsync();
        }
    }

    private void pauseMusic(MusicClipView musicClipView) {
        if (mPlayerWrapper.isPlaying()) {
            //mPlayerWrapper.updateMusicClipView(musicClipView);
            mPlayerWrapper.pause();
        }
    }

    private void pauseMusic(AppCompatSeekBar seekBar) {
        if (mPlayerWrapper.isPlaying()) {
            //mPlayerWrapper.updateSeekBar(seekBar);
            mPlayerWrapper.pause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayerWrapper.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerWrapper.destroy();
    }

    public interface IMusicDeleteListener {
        void onMusicDelete(Voice voice);
    }
}
