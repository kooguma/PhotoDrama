package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.SystemService;
import com.loopeer.android.photodrama4android.databinding.ActivityFeedBackBinding;
import com.loopeer.android.photodrama4android.model.validator.FeedbackValidator;
import com.loopeer.databindpack.validator.ObservableValidator;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class FeedbackActivity extends PhotoDramaBaseActivity {

    private FeedbackValidator mValidator;
    private MenuItem mSubmitItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFeedBackBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_feed_back);
        binding.setValidator(mValidator = new FeedbackValidator());
        mValidator.setEnableListener(b -> mSubmitItem.setEnabled(b));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        mSubmitItem = menu.findItem(R.id.menu_submit);
        mSubmitItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_submit) {
            onBtnClick(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_feed_back);
    }

    public void onBtnClick(View view) {
        if (mValidator.isValidated()) {
            Analyst.suggestSubmitClick();
            registerSubscription(
                    SystemService.INSTANCE.feedback(mValidator.getFeedback())
                            .filter(BaseResponse::isSuccessed)
                            .doOnNext(aVoid -> dismissProgressLoading())
                            .doOnNext(aVoid -> {
                                showToast(R.string.feedback_success);
                                finish();
                            })
                            .subscribe()
            );
        }
    }
}
