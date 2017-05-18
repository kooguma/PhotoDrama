package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.laputapp.ui.decorator.DividerItemDecoration;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.adapter.MyDownloadMusicAdapter;
import com.loopeer.android.photodrama4android.ui.hepler.MediaPlayerWrapper;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import io.reactivex.Flowable;
import java.io.File;
import java.util.List;

public class MyDownloadMusicFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Voice>,
    MyDownloadMusicAdapter.IMusicAdapter {

    private MyDownloadMusicAdapter mAdapter;
    private MediaPlayerWrapper mPlayerWrapper;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayerWrapper = new MediaPlayerWrapper(getContext());
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);   getRecyclerManager().getRecyclerView()
            .addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, 0,
                DeviceScreenUtils.dp2px(0.5f, getContext())));
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public RxRecyclerAdapter<Voice> createRecyclerViewAdapter() {
        MyDownloadMusicAdapter adapter = new MyDownloadMusicAdapter(getContext());
        adapter.setIMusicAdapter(this);
        return adapter;
    }

    @Override
    public Flowable<? extends BaseResponse<List<Voice>>> requestData(String offset, String page, String pageSize) {
        getRecyclerManager().onCacheLoaded(FileManager.getInstance().getAudioFiles());
        return null;
    }

    @Override public void onMusicAddClick(Voice voice) {

    }

    @Override
    public void onControllerShow(TextView txtStart, TextView txtCur, TextView txtEnd) {
        mPlayerWrapper.updateController(txtStart, txtCur, txtEnd);
    }

    @Override public void onMusicPlayClick(String path, MusicClipView musicClipView) {
        if (mPlayerWrapper.isAlreadyPrepared(new File(path))) {
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

    @Override public void onMusicPauseClick(String path, MusicClipView musicClipView) {
        mPlayerWrapper.updateMusicClipView(musicClipView);
        mPlayerWrapper.pause();
    }

    @Override public void onPause() {
        super.onPause();
        mPlayerWrapper.pause();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mPlayerWrapper.destroy();
    }

}
