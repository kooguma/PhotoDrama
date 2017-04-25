package com.loopeer.android.photodrama4android.api.sign;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Request;

public abstract class RequestReformer {

    private Request mPreRequest;
    private String mMd5key;
    private String mSign;

    public RequestReformer(Request preRequest, String md5key) {
        mPreRequest = preRequest;
        mMd5key = md5key;
    }

    protected abstract Request createNewRequest();

    protected Request getPreRequest() {
        return mPreRequest;
    }

    protected Request.Builder getReformedWithUrlRequestBuilder() {
        Request.Builder builder = new Request.Builder()
                .headers(getPreRequest().headers())
                .cacheControl(getPreRequest().cacheControl())
                .tag(getPreRequest().tag());
        HttpUrl url = createEncodeUrl();
        builder.url(url);
        return builder;
    }

    protected HttpUrl createEncodeUrl() {
        return getPreRequest().url();
    }

    protected Map<String, Object> getQueryMap() {
        HttpUrl httpUrl = getPreRequest().url();
        IdentityHashMap<String, Object> result = new IdentityHashMap<>();
        Set<String> nameSet = httpUrl.queryParameterNames();
        Iterator<String> iterator = nameSet.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            List<String> values = httpUrl.queryParameterValues(name);
            for (String value : values) {
                result.put(name, value);
            }
        }
        return result;
    }

    protected Map<String, Object> createTimeAndSignMap(Map<String, Object> map) {
        IdentityHashMap<String, Object> result = new IdentityHashMap<>();
//        result.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        Set<Map.Entry<String, Object>> set = map.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            result.put(entry.getKey(), entry.getValue());
        }
        mSign = createSignValue(result);
//        result.put("sign", createSignValue(result));
        return result;
    }

    private String createSignValue(IdentityHashMap<String, Object> map) {
        return SignUtils.apiEncryptSign(map, mMd5key);
    }

    public String getSign(){
        return mSign;
    }
}
