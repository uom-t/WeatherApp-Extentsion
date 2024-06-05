package com.example.weatherapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.api.NetworkResponse;
import com.example.weatherapp.api.WeatherModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;import retrofit2.http.GET;import retrofit2.http.Query;

public class WeatherViewModel extends ViewModel {
    private static final String BASE_URL = "https://api.weatherapi.com";
    private static final String API_KEY = "b87c22fca7c845a8a1052045240506";

    private MutableLiveData<NetworkResponse<WeatherModel>> weatherResult = new MutableLiveData<>();

    public LiveData<NetworkResponse<WeatherModel>> getWeatherResult() {
        return weatherResult;
    }

    public void fetchWeatherData(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherModel> call = weatherApi.getWeather(API_KEY, latitude + "," + longitude);

        weatherResult.setValue(new NetworkResponse.Loading<>());

        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if (response.isSuccessful()) {
                    weatherResult.setValue(new NetworkResponse.Success<>(response.body()));
                } else {
                    weatherResult.setValue(new NetworkResponse.Error<>(response.message()));
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                weatherResult.setValue(new NetworkResponse.Error<>(t.getMessage()));
            }
        });
    }
public static interface WeatherApi {
    @GET("v1/current.json")
    Call<WeatherModel> getWeather(@Query("key") String apiKey, @Query("q") String location);
}}