package com.loopeer.android.photodrama4android.api.sign;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SignRequestInterceptor implements Interceptor {

    private static final String TAG = "SignInterceptor";

    private String md5key;

    public SignRequestInterceptor(String md5key) {
        this.md5key = md5key;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestReformerManager requestReformerManager = new RequestReformerManager(request, md5key);
        Request reformRequest = requestReformerManager.createNewRequest();
        Response response = chain.proceed(reformRequest);
        return response;
    }

}
