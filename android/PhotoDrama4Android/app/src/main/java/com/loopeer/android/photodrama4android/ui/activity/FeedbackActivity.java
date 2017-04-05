package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityFeedBackBinding;
import com.loopeer.android.photodrama4android.model.validator.FeedbackValidator;

public class FeedbackActivity extends MovieMakerBaseActivity {

    private FeedbackValidator mValidator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFeedBackBinding binding = DataBindingUtil.setContentView(this,
            R.layout.activity_feed_back);
        binding.setValidator(mValidator = new FeedbackValidator());
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_feed_back);
    }
}
