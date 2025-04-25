package com.example.moviesapp.Api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static volatile Retrofit retrofit;
    private static String BASE_URL = "http://192.168.1.3:3000/";

    public static void setBaseUrl(String url) {
        BASE_URL = url;
        retrofit = null;
    }

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .addInterceptor(new Interceptor() {
                                @Override
                                public okhttp3.Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();
                                    SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                    String token = prefs.getString("token", "");
                                    Request.Builder builder = original.newBuilder();
                                    if (!token.isEmpty()) {
                                        builder.header("Authorization", "Bearer " + token);
                                    }
                                    Request request = builder.build();
                                    return chain.proceed(request);
                                }
                            })
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit.create(ApiService.class);
    }
}