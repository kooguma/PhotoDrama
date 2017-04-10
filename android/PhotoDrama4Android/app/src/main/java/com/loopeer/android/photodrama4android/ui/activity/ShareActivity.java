package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;

public class ShareActivity extends PhotoDramaBaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_share);
    }

    public void onBackToMain(View view) {
        Navigator.startMainActivity(this);
    }
}
