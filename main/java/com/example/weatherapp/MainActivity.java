package com.example.weatherapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherapp.api.NetworkResponse;
import com.example.weatherapp.api.WeatherModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private WeatherViewModel weatherViewModel;
    private FusedLocationProviderClient fusedLocationClient;

    private EditText editTextAddress;
    private Button buttonSearch;
    private TextView textViewLatLng;
    private TextView textViewAddress;
    private TextView textViewTime;
    private TextView textViewTemperature;
    private TextView textViewHumidity;
    private TextView textViewWeatherDescription;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = findViewById(R.id.edit_text_address);
        buttonSearch = findViewById(R.id.button_search);
        textViewLatLng = findViewById(R.id.text_view_lat_lng);
        textViewAddress = findViewById(R.id.text_view_address);
        textViewTime = findViewById(R.id.text_view_time);
        textViewTemperature = findViewById(R.id.text_view_temperature);
        textViewHumidity = findViewById(R.id.text_view_humidity);
        textViewWeatherDescription = findViewById(R.id.text_view_weather_description);

        // Initialize handler
        handler = new Handler(Looper.getMainLooper());

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        updateTime();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = editTextAddress.getText().toString();
                fetchLocation(address);
            }
        });

        weatherViewModel.getWeatherResult().observe(this, result -> {
            if (result instanceof NetworkResponse.Success) {
                WeatherModel weatherData = ((NetworkResponse.Success<WeatherModel>) result).getData();
                displayWeatherInfo(weatherData);
            } else if (result instanceof NetworkResponse.Error) {
                textViewWeatherDescription.setText(((NetworkResponse.Error) result).getMessage());
            }
        });
    }

    private void updateTime() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                textViewTime.setText("Time: " + currentTime);
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void fetchLocation(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String addressLine = location.getAddressLine(0);

                textViewLatLng.setText("Lat: " + latitude + ", Lng: " + longitude);
                textViewAddress.setText("Address: " + addressLine);

                weatherViewModel.fetchWeatherData(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayWeatherInfo(WeatherModel weatherData) {
        if (weatherData == null || weatherData.getCurrent() == null) {
            textViewWeatherDescription.setText("No weather data available");
            return;
        }

        Double tempC = weatherData.getCurrent().getTempC();
        Integer humidity = weatherData.getCurrent().getHumidity();
        String description = weatherData.getCurrent().getCondition().getText();

        textViewTemperature.setText("Temperature: " + (tempC != null ? tempC + "°C" : "N/A"));
        textViewHumidity.setText("Humidity: " + (humidity != null ? humidity + "%" : "N/A"));
        textViewWeatherDescription.setText("Description: " + (description != null ? description : "N/A"));
    }
}
