package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.model.Drama;
import com.loopeer.android.photodrama4android.ui.adapter.DramaSelectAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class DramaSelectFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Drama>, OnItemClickListener<Drama> {

    private DramaSelectAdapter mSelectAdapter;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectAdapter = new DramaSelectAdapter(getContext());
        mSelectAdapter.setOnItemClickListener(this);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drama_select, container, false);
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public BaseFooterAdapter<Drama> createRecyclerViewAdapter() {
        if (mSelectAdapter == null) {
            mSelectAdapter = new DramaSelectAdapter(getContext());
            mSelectAdapter.setOnItemClickListener(this);
        }
        return mSelectAdapter;
    }

    @Override
    public Observable<BaseResponse<List<Drama>>> requestData(String page, String pageSize) {
        List<Drama> dramas = new ArrayList<>();
        Drama drama = new Drama();
        drama.image
            = "http://i2.hdslb.com/bfs/archive/d88114babdbe68083378a896fece513d07fb7a46.jpg";
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        getRecyclerManager().onCacheLoaded(dramas);
        return null;
    }

    @Override public void onItemClick(Drama drama) {
        Navigator.startDramaDetailActivity(getContext());
    }
}
