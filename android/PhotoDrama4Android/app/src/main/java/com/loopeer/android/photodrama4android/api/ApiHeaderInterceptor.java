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
        RequestReformerManager reformerManager = new RequestReformerManager(originalRequest, BuildConfig.API_KEY);
        Request.Builder requestBuilder = reformerManager.createNewRequest().newBuilder()
                .addHeader("build", sVersionCode)
                .addHeader("version-name", sVersion)
                .addHeader("platform", "android")
                .addHeader("device-id", sDeviceId)
                .addHeader("channel-id", sChannelId)
                .addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                .addHeader("sign", reformerManager.getSign());

        return chain.proceed(requestBuilder.build());
    }
}
