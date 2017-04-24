package com.loopeer.android.photodrama4android.analytics;

import java.util.Map;

interface Analytics {
    void logEvent(String key);

    void logEvent(String key, Map<String, String> params);
}
