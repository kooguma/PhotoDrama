package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Voice;
import io.reactivex.Flowable;
import java.util.List;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VoiceService {

    VoiceService INSTANCE = ApiService.getRetrofit().create(VoiceService.class);

    @GET("voices")
    Flowable<BaseResponse<List<Voice>>> voices(@Query("category_id") String category_id);

    @FormUrlEncoded
    @POST("voices/download")
    Flowable<BaseResponse<Void>> download(@Field("voice_id") String voice_id);

}
