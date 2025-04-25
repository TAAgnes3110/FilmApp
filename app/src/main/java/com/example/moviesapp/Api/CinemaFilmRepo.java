package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.FilmMovieItems;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CinemaFilmRepo {
    private CinemaFilm apiService;

    public CinemaFilmRepo() {
        apiService = CinemaFilmRetrofitClient.createService(CinemaFilm.class);
    }

    public void getMovies(Callback<List<FilmMovieItems>> callback) {
        Call<List<FilmMovieItems>> call = apiService.getMovies();
        call.enqueue(callback);
    }
}