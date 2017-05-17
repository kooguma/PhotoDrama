package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.CacheResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Category;
import io.reactivex.processors.PublishProcessor;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoryService {
    CategoryService INSTANCE = ApiService.getRetrofit().create(CategoryService.class);

    String TYPE_MODEL = "0";
    String TYPE_SOUND_EFFECT = "1";
    String TYPE_SOUND_BGM = "2";

    @GET("categories")
    Flowable<CacheResponse<List<Category>>> categories(@Query("type") String type);
}
