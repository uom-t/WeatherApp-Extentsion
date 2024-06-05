package com.example.weatherapp.api;



import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("v1/current.json")
    Call<WeatherModel> getWeather(@Query("key") String apiKey, @Query("q") String location);
}
