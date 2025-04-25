package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.AddFavouriteRequest;
import com.example.moviesapp.Domain.FavouriteResponse;
import com.example.moviesapp.Domain.FilmRecommendationResponse;
import com.example.moviesapp.Domain.FilmResponse;
import com.example.moviesapp.Domain.GenreResponse;
import com.example.moviesapp.Domain.MovieDetailsResponse;
import com.example.moviesapp.Domain.MovieSearchResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class ApiMovieRepo {
    private ApiMovie apiService;

    public ApiMovieRepo() {
        apiService = ApiMovieRetrofitClient.createService(ApiMovie.class);
    }

    public void getListFilms(int page, int limit, String lastDocId, Callback<FilmResponse> callback) {
        Call<FilmResponse> call = apiService.getListFilms(page, limit, lastDocId);
        call.enqueue(callback);
    }

    public void getListFilmsRecommend(int page, int limit, String lastDocId, Callback<FilmResponse> callback) {
        Call<FilmResponse> call = apiService.getListFilmsRecommend(page, limit, lastDocId);
        call.enqueue(callback);
    }

    public void searchMovies(String query, boolean all, Callback<MovieSearchResponse> callback) {
        Call<MovieSearchResponse> call = apiService.searchMovies(query, all);
        call.enqueue(callback);
    }

    public void getMovieDetails(String movieId, Callback<MovieDetailsResponse> callback) {
        Call<MovieDetailsResponse> call = apiService.getMovieDetails(movieId);
        call.enqueue(callback);
    }

    public void getAllGenres(Callback<GenreResponse> callback) {
        Call<GenreResponse> call = apiService.getAllGenres();
        call.enqueue(callback);
    }

    public void getMoviesByGenre(String genreName, int page, int limit, String lastDocId, Callback<FilmResponse> callback) {
        Call<FilmResponse> call = apiService.getMoviesByGenre(genreName, page, limit, lastDocId);
        call.enqueue(callback);
    }

    public void getFavouriteMovies(String userId, int page, int limit, String lastDocId, Callback<FavouriteResponse> callback) {
        Call<FavouriteResponse> call = apiService.getFavouriteMovies(userId, page, limit, lastDocId);
        call.enqueue(callback);
    }

    public void addFavouriteMovie(String userId, AddFavouriteRequest body, Callback<Void> callback) {
        Call<Void> call = apiService.addFavouriteMovie(userId, body);
        call.enqueue(callback);
    }

    public void removeFavouriteMovie(String userId, String movieId, Callback<Void> callback) {
        Call<Void> call = apiService.removeFavouriteMovie(userId, movieId);
        call.enqueue(callback);
    }

    public Call<FilmRecommendationResponse> getRecommendations(RecommendationRequest request) {
        return apiService.getRecommendations(request);
    }
}