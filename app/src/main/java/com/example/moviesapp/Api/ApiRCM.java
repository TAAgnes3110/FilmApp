package com.example.moviesapp.Api;

import com.example.moviesapp.data.model.FilmRecommendationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiRCM {
    @POST("api/movies/recommend")
    Call<FilmRecommendationResponse> getRecommendations(@Body RecommendationRequest request);
}
