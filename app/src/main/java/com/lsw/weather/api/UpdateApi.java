package com.lsw.weather.api;

import com.lsw.weather.model.WeatherEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by liushuwei on 2017/4/10.
 */

public interface UpdateApi {
    @GET("/apps/latest/{applicationId}")
    Observable<WeatherEntity> getUpdateInfo(@Path("applicationId") String applicationId, @Query("api_token") String api_token);
}
