package com.example.moviesapp.Api;

import com.example.moviesapp.data.model.AddFavouriteRequest;
import com.example.moviesapp.data.model.FavouriteResponse;
import com.example.moviesapp.data.model.FilmResponse;
import com.example.moviesapp.data.model.GenreResponse;
import com.example.moviesapp.data.model.MovieDetailsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiMovie {
    String BASE_URL = "http://192.168.32.101:3000/"; // Địa chỉ server Node.js của bạn

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiMovie apiMovie = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiMovie.class);

    @GET("api/movies")
    Call<FilmResponse> getListFilms(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("lastDocId") String lastDocId // Thêm lastDocId
    );

    @POST("api/movies/recommend")
    Call<FilmResponse> getListFilmsRecommend(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("lastDocId") String lastDocId // Thêm lastDocId
    );

    @GET("api/movies/search")
    Call<FavouriteResponse> searchMovies(
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
}