package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.PermissionUtils;

public class MainActivity extends PhotoDramaBaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_main);
        PermissionUtils.checkStoragePermission(this);

      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    findViewById(R.id.btn_act).setSystemUiVisibility(*//*findViewById(R.id.btn_act).getSystemUiVisibility() | *//*View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }, 100);*/
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
        Navigator.startDramaSelectActivity(this);
    }

    public void onCreateClick(View view) {
        Analyst.homeMyCreatClick();
        Navigator.startImageSelectActivity(this);
    }

    public void onSettingClick(View view) {
        Analyst.homeSettingClick();
        Navigator.startSettingActivity(this);
    }

}
