package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.ShowtimeItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class TimeCinemaFilmRepo {
    private TimeCinemaFilm apiService;

    public TimeCinemaFilmRepo() {
        apiService = TimeCinemaFilmRetrofitClient.createService(TimeCinemaFilm.class);
    }

    public void getShowtimes(String movieName, String showDate, Callback<List<ShowtimeItem>> callback) {
        Call<List<ShowtimeItem>> call = apiService.getShowtimes(movieName, showDate);
        call.enqueue(callback);
    }
}