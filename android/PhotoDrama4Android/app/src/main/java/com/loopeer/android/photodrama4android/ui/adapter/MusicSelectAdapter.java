package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.model.Music;

public class MusicSelectAdapter extends BaseFooterAdapter<Music> {
    public MusicSelectAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Music music, int pos, RecyclerView.ViewHolder holder) {

    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        return null;
    }
}
