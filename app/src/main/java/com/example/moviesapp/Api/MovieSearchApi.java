package com.example.moviesapp.Api;

import com.example.moviesapp.data.model.MovieSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieSearchApi {
    @GET("api/movies/search")
    Call<MovieSearchResponse> searchMovies(
            @Query("query") String query,
            @Query("all") boolean all
    );
}