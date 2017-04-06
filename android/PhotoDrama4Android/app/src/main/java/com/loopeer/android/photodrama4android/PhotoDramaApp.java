package com.loopeer.android.photodrama4android;

import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.utils.Toaster;

public class PhotoDramaApp extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Toaster.init(this);
        BitmapFactory.init(this);
        ApiService.init(this);
        ResponseObservable.init(this);
    }
}
