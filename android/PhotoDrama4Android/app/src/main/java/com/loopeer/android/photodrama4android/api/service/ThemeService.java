package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.laputapp.http.CacheResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Theme;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ThemeService {
    ThemeService INSTANCE = ApiService.getRetrofit().create(ThemeService.class);

    //v1.2.2
    @Deprecated
    @GET("themes/list")
    Flowable<CacheResponse<List<Theme>>> list(
            @Query("category_id") String categoryId,
            @Query("page") String page,
            @Query("page_size") String pageSize
    );

    @FormUrlEncoded
    @POST("themes/share")
    Flowable<BaseResponse<Void>> share(
            @Field("theme_id") String themeId
    );

}
