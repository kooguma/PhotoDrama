package com.loopeer.android.photodrama4android.model.validator;

import android.databinding.Bindable;
import android.text.TextUtils;
import android.util.Log;
import com.loopeer.databindpack.validator.ObservableValidator;

public class FeedbackValidator extends ObservableValidator {

    public String feedback;

    @Bindable
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
        notifyEnable();
    }

    @Override public boolean checkEnable() {
        return !TextUtils.isEmpty(feedback);
    }

    @Override
    public boolean isValidated() {
        return !TextUtils.isEmpty(feedback);
    }
}
