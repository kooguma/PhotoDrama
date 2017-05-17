package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.laputapp.ui.decorator.DividerItemDecoration;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.service.CategoryService;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.adapter.MusicRecommendAdapter;
import io.reactivex.Flowable;
import java.util.List;

public class RecommendMusicFragment extends MovieMakerBaseFragment implements IPageRecycler<Category> {

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerManager().getRecyclerView()
            .addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST,
                DeviceScreenUtils.dp2px(16,getContext()),0,DeviceScreenUtils.dp2px(0.5f,getContext())));
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public RxRecyclerAdapter<Category> createRecyclerViewAdapter() {
        return new MusicRecommendAdapter(getContext());
    }

    @Override
    public Flowable<? extends BaseResponse<List<Category>>> requestData(String page, String pageSize) {
        return CategoryService.INSTANCE.categories(CategoryService.TYPE_SOUND_BGM);
    }
}
