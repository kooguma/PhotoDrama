package com.loopeer.android.photodrama4android;


import com.laputapp.Laputapp;
import com.loopeer.android.photodrama4android.opengl.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.utils.Toaster;

public class PhotoDramaApp extends Laputapp {

    @Override
    public void onCreate() {
        super.onCreate();

        Toaster.init(this);
        BitmapFactory.init(this);
    }
}
