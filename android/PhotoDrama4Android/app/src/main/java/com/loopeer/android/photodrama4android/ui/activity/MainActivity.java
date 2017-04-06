package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.PermissionUtils;

public class MainActivity extends MovieMakerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.onRequestPermissionsResult(this, requestCode, grantResults)) {
            FileManager.getInstance().init();
        }
    }

    public void onActClick(View view) {
      Navigator.startDramaSelectActivity(this);
    }

    public void onCreateClick(View view) {
        Navigator.startImageSelectActivity(this);
    }

    public void onSettingClick(View view) {
        Navigator.startSettingActivity(this);
    }
}
