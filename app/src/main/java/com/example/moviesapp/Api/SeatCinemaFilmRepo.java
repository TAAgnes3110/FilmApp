package com.example.moviesapp.Api;

import retrofit2.Call;
import retrofit2.Callback;

public class SeatCinemaFilmRepo {
    private SeatCinemaFilm apiService;

    public SeatCinemaFilmRepo() {
        apiService = SeatCinemaFilmRetrofitClient.createService(SeatCinemaFilm.class);
    }

    public void getSeats(String movieName, String showDate, String showTime, Callback<SeatResponse> callback) {
        Call<SeatResponse> call = apiService.getSeats(movieName, showDate, showTime);
        call.enqueue(callback);
    }
}