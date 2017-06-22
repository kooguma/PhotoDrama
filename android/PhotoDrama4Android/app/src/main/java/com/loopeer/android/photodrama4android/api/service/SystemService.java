package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Version;

import com.loopeer.android.photodrama4android.model.Advert;
import io.reactivex.Flowable;
import java.util.List;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SystemService {

    SystemService INSTANCE = ApiService.getRetrofit().create(SystemService.class);

    @FormUrlEncoded
    @POST("system/feedback")
    Flowable<BaseResponse<Void>> feedback(@Field("content") String feedback);

    @GET("system/launchAd")
    Flowable<BaseResponse<Advert>> launchAd();

    @GET("system/listAd")
    Flowable<BaseResponse<List<Advert>>> listAd();

    @GET("system/version")
    Flowable<BaseResponse<Version>> version();

}
