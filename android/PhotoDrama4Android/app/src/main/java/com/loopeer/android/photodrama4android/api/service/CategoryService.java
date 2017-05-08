package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.CacheResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Category;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface CategoryService {
    CategoryService INSTANCE = ApiService.getRetrofit().create(CategoryService.class);

    @GET("categories")
    Flowable<CacheResponse<List<Category>>> categories();
}
