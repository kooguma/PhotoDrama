package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.service.ThemeService;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.adapter.DramaSelectAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class DramaSelectFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Theme>, OnItemClickListener<Theme> {

    private static final String KEY_CATEGORY_ID = "CATEGORY_ID";

    private DramaSelectAdapter mSelectAdapter;
    private String mCategoryId;

    public static DramaSelectFragment newDramaSelectFragment(String categoryId) {
        DramaSelectFragment fragment = new DramaSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CATEGORY_ID, categoryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryId = getArguments().getString(KEY_CATEGORY_ID);
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

    @Override public BaseFooterAdapter<Theme> createRecyclerViewAdapter() {
        if (mSelectAdapter == null) {
            mSelectAdapter = new DramaSelectAdapter(getContext());
            mSelectAdapter.setOnItemClickListener(this);
        }
        return mSelectAdapter;
    }

    @Override
    public Observable<BaseResponse<List<Theme>>> requestData(String page, String pageSize) {
        return ThemeService.INSTANCE.list(mCategoryId);
    }

    @Override public void onItemClick(Theme theme) {
        Navigator.startDramaDetailActivity(getContext(),theme);
    }
}
