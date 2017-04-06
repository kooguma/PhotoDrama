package com.loopeer.android.photodrama4android.api;

import com.laputapp.Laputapp;
import com.laputapp.http.sign.RequestReformerManager;
import com.loopeer.android.photodrama4android.BuildConfig;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiHeaderInterceptor implements Interceptor {

    private static final String sVersion = Laputapp.getAppInfo().version;
    private static final String sVersionCode = Laputapp.getAppInfo().versionCode;
    private static final String sDeviceId = Laputapp.getAppInfo().deviceId;
    private static final String sChannelId = Laputapp.getAppInfo().channel;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        //Request originalRequest = chain.request();
        RequestReformerManager reformerManager = new RequestReformerManager(originalRequest,"");
       // Request newRequest = originalRequest.createNewRequest();
        Request.Builder requestBuilder = originalRequest.newBuilder()
            .addHeader("build", sVersionCode)
            .addHeader("version_name", sVersion)
            .addHeader("platform", "android")
            .addHeader("device_id", sDeviceId)
            .addHeader("channel_id", sChannelId);
        //.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000))

        return chain.proceed(requestBuilder.build());
    }
}
