package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.ShowtimeItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TimeCinemaFilm {
    @GET("theaters/search")
    Call<List<ShowtimeItem>> getShowtimes(
            @Query("movie_name") String movieName,
            @Query("show_date") String showDate
    );
}