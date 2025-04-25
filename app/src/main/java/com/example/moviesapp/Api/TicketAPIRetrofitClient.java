package com.example.moviesapp.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketAPIRetrofitClient {
    private static final String BASE_URL = "http://192.168.1.3:6000/"; // Emulator
    private static Retrofit retrofit;

    public static TicketAPI getTicketAPI() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(TicketAPI.class);
    }
}