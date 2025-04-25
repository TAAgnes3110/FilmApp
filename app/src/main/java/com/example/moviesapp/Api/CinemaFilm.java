package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.FilmMovieItems;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CinemaFilm {
    @GET("movies")
    Call<List<FilmMovieItems>> getMovies();
}