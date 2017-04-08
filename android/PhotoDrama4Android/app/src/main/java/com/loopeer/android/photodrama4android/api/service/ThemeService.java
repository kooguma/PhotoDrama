package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Theme;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ThemeService {
    ThemeService INSTANCE = ApiService.getRetrofit().create(ThemeService.class);

    @GET("/api/v1/themes/list")
    Flowable<BaseResponse<List<Theme>>> list(@Query("category_id") String categoryId);

}