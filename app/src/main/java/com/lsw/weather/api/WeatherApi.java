package com.lsw.weather.api;

import com.lsw.weather.model.WeatherEntity;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherApi {
    @GET("weather")
    Observable<WeatherEntity> getWeather(@Query("city") String city, @Query("key") String key);
}
