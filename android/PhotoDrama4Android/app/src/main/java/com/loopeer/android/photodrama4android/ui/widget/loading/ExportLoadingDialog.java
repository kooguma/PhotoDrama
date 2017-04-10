package com.loopeer.android.photodrama4android.ui.widget.loading;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.FloatRange;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.laputapp.utilities.UiUtilities;
import com.loopeer.android.photodrama4android.R;

public class ExportLoadingDialog extends Dialog {

    private ExportLoadingProgress mProgressBar;
    private TextView mMessageTextView;
    private TextView mProgressTextView;

    private Window mWindow;

    private CharSequence mMessage;
    private boolean mShowProgress = true;

    public ExportLoadingDialog(Context context, int theme) {
        super(context, theme);
        initialize(context, theme);
    }

    private void initialize(Context context, int theme) {
        mWindow = getWindow();
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        mWindow.setBackgroundDrawable(null);
        setContentView(R.layout.progress_export_loading);
        mMessageTextView = (TextView) findViewById(R.id.text_message);
        mProgressTextView = (TextView) findViewById(R.id.text_progress);
        mProgressBar = (ExportLoadingProgress) findViewById(R.id.progress);
    }

    public ExportLoadingDialog setMessage(int resId) {
        return setMessage(getContext().getString(resId));
    }

    public ExportLoadingDialog setMessage(CharSequence msg) {
        mMessage = msg;
        return this;
    }

    public ExportLoadingDialog updateMessage(CharSequence msg) {
        mMessage = msg;
        show();
        return this;
    }

    public ExportLoadingDialog hideMessage() {
        return updateMessage("");
    }

    public ExportLoadingDialog hideProgressBar() {
        mShowProgress = false;
        show();
        return this;
    }

    @Override
    public void show() {
        if (TextUtils.isEmpty(mMessage)) {
            UiUtilities.setVisibilitySafe(mMessageTextView, View.GONE);
        } else {
            UiUtilities.setVisibilitySafe(mMessageTextView, View.VISIBLE);
            mMessageTextView.setText(mMessage);
        }

        if (mShowProgress) {
            UiUtilities.setVisibilitySafe(mProgressBar, View.VISIBLE);
        } else {
            UiUtilities.setVisibilitySafe(mProgressBar, View.GONE);
        }

        super.show();
    }

    public void setProgress(@FloatRange(from = 0f, to = 1f) float progress) {
        mProgressBar.setProgress(progress);
        mProgressTextView.setText((int)(progress * 100) + "%");
    }

}