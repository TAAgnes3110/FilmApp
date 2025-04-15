package com.example.moviesapp.Api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SeatCinemaFilm {
    @GET("theaters/seats")
    Call<SeatResponse> getSeats(
            @Query("movie_name") String movieName,
            @Query("show_date") String showDate,
            @Query("show_time") String showTime
    );
}