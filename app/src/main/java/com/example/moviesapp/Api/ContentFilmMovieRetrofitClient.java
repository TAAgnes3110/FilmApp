package com.example.moviesapp.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContentFilmMovieRetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.1.3:6000"; // Thay bằng base URL thực tế của bạn

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}