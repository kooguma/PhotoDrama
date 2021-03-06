package com.loopeer.android.photodrama4android.api.service;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.api.ApiService;
import com.loopeer.android.photodrama4android.model.Series;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SeriesService {
    SeriesService INSTANCE = ApiService.getRetrofit().create(SeriesService.class);

    @GET("series/detail")
    Flowable<BaseResponse<Series>> detail(@Query("series_id") String seriesId);

    @GET("series/list")
    Flowable<BaseResponse<List<Series>>> list(
        @Query("category_id") String categoryId,
        @Query("page") String page,
        @Query("page_size") String pageSize
    );
}
