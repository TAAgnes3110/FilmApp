package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.AddFavouriteRequest;
import com.example.moviesapp.Domain.FavouriteResponse;
import com.example.moviesapp.Domain.FilmRecommendationResponse;
import com.example.moviesapp.Domain.FilmResponse;
import com.example.moviesapp.Domain.GenreResponse;
import com.example.moviesapp.Domain.MovieDetailsResponse;
import com.example.moviesapp.Domain.MovieSearchResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiMovie {
    @GET("api/movies")
    Call<FilmResponse> getListFilms(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("lastDocId") String lastDocId
    );

    @POST("api/movies/recommend")
    Call<FilmResponse> getListFilmsRecommend(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("lastDocId") String lastDocId
    );

    @GET("api/movies/search")
    Call<MovieSearchResponse> searchMovies(
            @Query("query") String query,
            @Query("all") boolean all
    );

    @GET("api/movies/{movieId}")
    Call<MovieDetailsResponse> getMovieDetails(
            @Path("movieId") String movieId
    );

    @GET("api/genres")
    Call<GenreResponse> getAllGenres();

    @GET("api/movies/genre/{genreName}")
    Call<FilmResponse> getMoviesByGenre(
            @Path("genreName") String genreName,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("lastDocId") String lastDocId
    );

    @GET("api/users/{userId}/favourites")
    Call<FavouriteResponse> getFavouriteMovies(
            @Path("userId") String userId,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("lastDocId") String lastDocId
    );

    @POST("api/users/{userId}/favourites")
    Call<Void> addFavouriteMovie(
            @Path("userId") String userId,
            @Body AddFavouriteRequest body
    );

    @DELETE("api/users/{userId}/favourites/{movieId}")
    Call<Void> removeFavouriteMovie(
            @Path("userId") String userId,
            @Path("movieId") String movieId
    );

    @POST("api/movies/recommend")
    Call<FilmRecommendationResponse> getRecommendations(@Body RecommendationRequest request);
}