package com.loopeer.android.photodrama4android.api;

import android.content.Context;

import com.laputapp.api.calladapter.LoopeerCallAdapterFactory;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.utils.gson.GsonHelper;
import java.io.File;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    public static final String API_URL = BuildConfig.API_URL;

    private static Retrofit sRetrofit;

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    public static void init(Context context) {
        context = context.getApplicationContext();
        sRetrofit = new Retrofit.Builder()
                .client(buildOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.getDefault()))
                .addCallAdapterFactory(LoopeerCallAdapterFactory.create(LocalCallAdapter.class, PhotoDramaApp.getAppContext()))
                .baseUrl(API_URL)
                .build();
    }

    private static OkHttpClient buildOkHttpClient(Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new ApiHeaderInterceptor());
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logInterceptor);
        }
        return httpClient.build();
    }

    public static Retrofit getRetrofit() {
        if (sRetrofit == null) {
            throw new IllegalStateException("call init() first");
        }
        return sRetrofit;
    }
}
