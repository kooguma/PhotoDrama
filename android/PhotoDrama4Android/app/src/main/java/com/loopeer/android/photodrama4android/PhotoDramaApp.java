package com.loopeer.android.photodrama4android;

import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.utils.Toaster;

public class PhotoDramaApp extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();

        Toaster.init(this);
        BitmapFactory.init(this);
    }
}
