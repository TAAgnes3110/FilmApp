package com.example.moviesapp.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiMovieRetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://192.168.1.3:4000/";
    private static final int TIMEOUT_SECONDS = 60;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (ApiMovieRetrofitClient.class) {
                if (retrofit == null) {
                    // Tạo Gson với định dạng ngày tùy chỉnh
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();

                    // Tạo HttpLoggingInterceptor để ghi log
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    // Tạo OkHttpClient với timeout và logging
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .build();

                    // Tạo Retrofit instance
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create(gson))
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