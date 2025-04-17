package com.example.moviesapp.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecommendationRequestRetrofitClient {
    private static final String BASE_URL = "http://192.168.32.101:3000/"; // Thay đổi nếu cần
    private static ApiRCM apiService;

    public static ApiRCM getApiService() {
        if (apiService == null) {
            // Thêm logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            // Cấu hình Gson để không serialize các trường null
            Gson gson = new GsonBuilder()
                    .serializeNulls() // Mặc định là false, nhưng chúng ta sẽ kiểm soát qua logic
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            apiService = retrofit.create(ApiRCM.class);
        }
        return apiService;
    }
}
