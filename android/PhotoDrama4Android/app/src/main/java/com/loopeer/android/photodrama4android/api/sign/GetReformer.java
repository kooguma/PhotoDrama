package com.loopeer.android.photodrama4android.api.sign;

import android.util.Log;

import com.laputapp.BuildConfig;

import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Request;

class GetReformer extends RequestReformer {

    public GetReformer(Request preRequest, String key) {
        super(preRequest, key);
    }

    @Override
    protected Request createNewRequest() {
        Request.Builder builder = getReformedWithUrlRequestBuilder();
        Request reformRequest = builder.build();

        if (BuildConfig.DEBUG) {
            Log.d("SignInterceptor", reformRequest.url().toString());
        }
        return builder.build();
    }

    @Override
    protected HttpUrl createEncodeUrl() {
        Map<String, Object> signMap = createTimeAndSignMap(getQueryMap());
        HttpUrl.Builder builder = getPreRequest().url().newBuilder();
        Set<Map.Entry<String, Object>> set = signMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            builder.removeAllQueryParameters(entry.getKey());
            builder.addQueryParameter(entry.getKey(), entry.getValue().toString());
        }
        return builder.build();
    }
}
