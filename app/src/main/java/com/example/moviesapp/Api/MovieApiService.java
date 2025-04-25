package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.FilmDetail;
import com.example.moviesapp.Domain.FilmMovieItems;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieApiService {
    // Endpoint: GET /movies
    @GET("movies")
    Call<List<FilmMovieItems>> getMovies();

    // Endpoint: GET /movies/{id}
    @GET("movies/{id}")
    Call<FilmDetail> getMovieDetails(@Path("id") String movieId);
}