package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityTransitionEditBinding;
import com.loopeer.android.photodrama4android.opengl.VideoPlayManagerContainer;
import com.loopeer.android.photodrama4android.opengl.VideoPlayerManager;
import com.loopeer.android.photodrama4android.opengl.model.Drama;
import com.loopeer.android.photodrama4android.opengl.model.TransitionClip;
import com.loopeer.android.photodrama4android.opengl.model.TransitionImageWrapper;
import com.loopeer.android.photodrama4android.opengl.model.TransitionType;
import com.loopeer.android.photodrama4android.opengl.utils.ClipsCreator;
import com.loopeer.android.photodrama4android.ui.adapter.ImageTransitionSegmentAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.TransitionEffectAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransitionEditActivity extends AppCompatActivity implements ImageTransitionSegmentAdapter.OnSelectedListener, TransitionEffectAdapter.OnSelectedListener {

    private ActivityTransitionEditBinding mBinding;
    private ImageTransitionSegmentAdapter mImageTransitionSegmentAdapter;
    private TransitionEffectAdapter mTransitionEffectAdapter;
    private Drama mDrama;
    private VideoPlayerManager mVideoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transition_edit);

        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);
        updateRecyclerView();

        mVideoPlayerManager = new VideoPlayerManager(null, mBinding.glSurfaceView, mDrama);
        VideoPlayManagerContainer.getDefault().putVideoManager(this, mVideoPlayerManager);

    }

    private void updateRecyclerView() {
        mTransitionEffectAdapter = new TransitionEffectAdapter(this);
        mTransitionEffectAdapter.setOnSelectedListener(this);
        mBinding.recyclerViewTransition.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerViewTransition.setAdapter(mTransitionEffectAdapter);
        mTransitionEffectAdapter.updateData(createTransitionClips());

        mImageTransitionSegmentAdapter = new ImageTransitionSegmentAdapter(this);
        mImageTransitionSegmentAdapter.setOnSelectedListener(this);
        mBinding.recyclerViewSegment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerViewSegment.setAdapter(mImageTransitionSegmentAdapter);
        mImageTransitionSegmentAdapter.updateData(ClipsCreator.createTransiImageWrappers(mDrama.videoGroup));

        mImageTransitionSegmentAdapter.selectedFirstTransition();
    }

    private List<TransitionClip> createTransitionClips() {
        List<TransitionClip> transitionClips = new ArrayList<>();
        TransitionType[] types = TransitionType.values();
        for (int i = 0; i < types.length; i++) {
            transitionClips.add(new TransitionClip(types[i]));
        }
        return transitionClips;
    }

    @Override
    public void onImageTransitionSelected(TransitionImageWrapper transitionImageWrapper) {
        if (transitionImageWrapper.isImageClip()) return;
        notifyEffect(transitionImageWrapper.transitionClip);
    }

    private void notifyEffect(TransitionClip transitionClip) {
        mTransitionEffectAdapter.selectedItem(transitionClip);
    }

    @Override
    public void onEffectSelected(TransitionClip transitionClip) {
        mImageTransitionSegmentAdapter.notifyTransition(transitionClip);
    }
}
