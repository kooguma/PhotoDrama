package com.loopeer.android.photodrama4android.analytics;

import android.app.Application;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.tendcloud.tenddata.TCAgent;

import java.util.Map;

public class TalkingDataAnalyticsImpl implements Analytics {

    public TalkingDataAnalyticsImpl(Application application, boolean catchException, boolean logOn) {
        TCAgent.LOG_ON = logOn;
        TCAgent.init(application);
        TCAgent.setReportUncaughtExceptions(catchException);
    }

    @Override
    public void logEvent(String key) {
        TCAgent.onEvent(PhotoDramaApp.getAppContext(), key);
    }

    @Override
    public void logEvent(String key, Map<String, String> params) {
        TCAgent.onEvent(PhotoDramaApp.getAppContext(), key, null, params);
    }
}
