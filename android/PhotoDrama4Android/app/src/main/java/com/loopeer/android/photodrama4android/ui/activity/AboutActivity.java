package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.loopeer.android.photodrama4android.R;

public class AboutActivity extends MovieMakerBaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this,R.layout.activity_about_us);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_about_us);
    }

    public void onRateClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
            Uri.parse("market://details?id=" + getPackageName()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.about_rank_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
