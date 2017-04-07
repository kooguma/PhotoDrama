package com.loopeer.android.photodrama4android.api.service;

import com.loopeer.android.photodrama4android.model.Series;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface SeriesService {

    @GET("/api/v1/series/detail")
    Flowable<List<Series>> detail(String seriesId);
}
