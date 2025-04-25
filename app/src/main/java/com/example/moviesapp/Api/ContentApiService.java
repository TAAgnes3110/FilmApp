package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.FilmDetail;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ContentApiService {
    @GET("contents/{id}")
    Call<FilmDetail> getContentDetails(@Path("id") String id);
}