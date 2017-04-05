package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import com.loopeer.android.photodrama4android.R;

public class AboutActivity extends MovieMakerBaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_about_us);
    }
}
