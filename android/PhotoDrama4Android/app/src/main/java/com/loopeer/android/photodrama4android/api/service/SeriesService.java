package com.loopeer.android.photodrama4android.api.service;

import com.loopeer.android.photodrama4android.model.Series;
import java.util.List;
import retrofit2.http.GET;
import rx.Observable;

public interface SeriesService {

    @GET("/api/v1/series/detail")
    Observable<List<Series>> detail(String seriesId);
}
