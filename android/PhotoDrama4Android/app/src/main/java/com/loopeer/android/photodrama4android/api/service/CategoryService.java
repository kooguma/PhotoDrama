package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Category;
import java.util.List;
import retrofit2.http.GET;
import rx.Observable;

public interface CategoryService {
    CategoryService INSTANCE = ApiService.getRetrofit().create(CategoryService.class);

    @GET("/api/v1/categories")
    Observable<BaseResponse<List<Category>>> categories();
}
