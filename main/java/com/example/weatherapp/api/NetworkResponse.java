package com.example.weatherapp.api;

public class NetworkResponse<T> {
    public static class Success<T> extends NetworkResponse<T> {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class Error<T> extends NetworkResponse<T> {
        private String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class Loading<T> extends NetworkResponse<T> {
    }
}
