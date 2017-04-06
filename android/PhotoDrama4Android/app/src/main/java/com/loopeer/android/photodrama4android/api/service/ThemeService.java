package com.loopeer.android.photodrama4android.api.service;

import com.loopeer.android.photodrama4android.model.Theme;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ThemeService {

    @GET("/api/v1/themes/list")
    Observable<List<Theme>> list(@Query("category_id") String categoryId);

}
