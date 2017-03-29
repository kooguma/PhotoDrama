package com.loopeer.android.photodrama4android.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.Constants;
import com.loopeer.android.photodrama4android.media.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectInnderImageView;
import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;

public class ScrollSelectAdapter extends ScrollSelectView.Adapter<TransitionImageWrapper> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.list_item_scroll_inner_item, parent, false);
    }

    @Override
    public void onBindView(View view, TransitionImageWrapper clip) {
        ScrollSelectInnderImageView innderImageView = (ScrollSelectInnderImageView) view;
        if (clip.isImageClip()) {
            innderImageView.updateImage(Constants.DEFAULT_IMAGE_CLIP_SHOW_TIME, clip.imageClip.showTime, clip.imageClip.path);
        } else {
            innderImageView.updateImage(Constants.DEFAULT_IMAGE_CLIP_SHOW_TIME, clip.transitionClip.showTime, clip.transitionPreImagePath);
        }
    }
}
