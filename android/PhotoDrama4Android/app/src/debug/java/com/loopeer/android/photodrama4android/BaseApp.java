package com.loopeer.android.photodrama4android;

import com.laputapp.Laputapp;
import com.loopeer.andebug.AnDebug;
import com.loopeer.android.photodrama4android.BuildConfig;

public class BaseApp extends Laputapp {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.FLAVOR.equals("developNoTools")) {
            AnDebug.install(this);
            //LeakCanary.install(this);
        }
        //Stetho.initializeWithDefaults(this);
    }

}
