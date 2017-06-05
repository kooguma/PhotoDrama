package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.PermissionUtils;
import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class MainActivity extends PhotoDramaBaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_main);
        PermissionUtils.checkStoragePermission(this);
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
        Analyst.homeDramaClick();
        if (FileManager.hasExternalStoragePermission(this)) {
            Navigator.startDramaSelectActivity(this);
        } else {
            showToast(R.string.common_storage_permission_fail);
        }
    }

    public void onCreateClick(View view) {
        Analyst.homeMyCreatClick();
        if (FileManager.hasExternalStoragePermission(this)) {
            Navigator.startImageSelectActivity(this);
        } else {
            showToast(R.string.common_storage_permission_fail);
        }
    }

    public void onSettingClick(View view) {
        Analyst.homeSettingClick();
        Navigator.startSettingActivity(this);
    }

}
