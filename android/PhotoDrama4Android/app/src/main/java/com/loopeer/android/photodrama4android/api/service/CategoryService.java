package com.loopeer.android.photodrama4android.api.service;

import com.loopeer.android.photodrama4android.model.Category;
import java.util.List;
import retrofit2.http.GET;
import rx.Observable;

public interface CategoryService {

    @GET("/api/v1/categories")
    Observable<List<Category>> categories();
}
