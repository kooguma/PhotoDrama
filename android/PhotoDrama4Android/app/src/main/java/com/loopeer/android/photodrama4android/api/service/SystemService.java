package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.api.ApiService;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SystemService {

    SystemService INSTANCE = ApiService.getRetrofit().create(SystemService.class);

    @FormUrlEncoded
    @POST("system/feedback")
    Flowable<BaseResponse<Void>> feedback(@Field("content") String feedback);

}