package com.example.moviesapp.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimeCinemaFilmRetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://192.168.1.3:6000/";
    private static final int TIMEOUT_SECONDS = 30;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (TimeCinemaFilmRetrofitClient.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static <T> T createService(Class<T> serviceClass) {
        return getInstance().create(serviceClass);
    }
}