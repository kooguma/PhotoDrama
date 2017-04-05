package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import com.loopeer.android.photodrama4android.R;

public class ShareActivity extends MovieMakerBaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_share);
    }
}
