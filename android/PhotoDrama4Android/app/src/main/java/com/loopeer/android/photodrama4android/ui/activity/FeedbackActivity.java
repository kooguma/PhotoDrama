package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.SystemService;
import com.loopeer.android.photodrama4android.databinding.ActivityFeedBackBinding;
import com.loopeer.android.photodrama4android.model.validator.FeedbackValidator;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class FeedbackActivity extends PhotoDramaBaseActivity {

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

    public void onBtnClick(View view) {
        if (mValidator.isValidated()) {
            Analyst.suggestSubmitClick();
            registerSubscription(
                    ResponseObservable
                            .unwrap(this, SystemService.INSTANCE.feedback(mValidator.getFeedback()))
                            .doOnNext(aVoid -> dismissProgressLoading())
                            .doOnNext(aVoid -> {
                                showToast("感谢您的反馈");
                                finish();
                            })
                            .subscribe()
            );
        }
    }
}
