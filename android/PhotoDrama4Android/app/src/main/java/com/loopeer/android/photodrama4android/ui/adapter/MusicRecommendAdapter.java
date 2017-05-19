package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemMusicRecommendBinding;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;

public class MusicRecommendAdapter extends BaseFooterAdapter<Category> {

    private MusicClip.MusicType mType;

    public MusicRecommendAdapter(Context context, MusicClip.MusicType type) {
        super(context);
        this.mType = type;
    }

    @Override public void bindItem(Category category, int var2, RecyclerView.ViewHolder holder) {
        ListItemMusicRecommendBinding binding =
            ((DataBindingViewHolder<ListItemMusicRecommendBinding>) holder).binding;
        binding.setCategory(category);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (category != null) {
                    Navigator.startMusicDownloadActivity(getContext(),mType,category);
                }
            }
        });
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = getLayoutInflater().inflate(R.layout.list_item_music_recommend, parent, false);
        return new DataBindingViewHolder<>(v);
    }

}
