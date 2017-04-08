package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Category;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface CategoryService {
    CategoryService INSTANCE = ApiService.getRetrofit().create(CategoryService.class);

    @GET("/api/v1/categories")
    Flowable<BaseResponse<List<Category>>> categories();
}