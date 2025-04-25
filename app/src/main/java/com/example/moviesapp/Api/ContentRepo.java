package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.FilmDetail;
import retrofit2.Call;
import retrofit2.Callback;

public class ContentRepo {
    private ContentApiService apiService;

    public ContentRepo() {
        apiService = ContentFilmMovieRetrofitClient.getClient().create(ContentApiService.class);
    }

    public void getContentDetails(String contentId, Callback<FilmDetail> callback) {
        Call<FilmDetail> call = apiService.getContentDetails(contentId);
        call.enqueue(callback);
    }
}