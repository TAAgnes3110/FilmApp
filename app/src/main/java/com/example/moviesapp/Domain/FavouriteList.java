package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

public class FavouriteList {
    @SerializedName("id")
    private String id;

    @SerializedName("movie_id")
    private String movieId;

    @SerializedName("movie_title")
    private String movieTitle;

    @SerializedName("added_at")
    private String addedAt;

    @SerializedName("movie_details")
    private FavouriteItem movieDetails;

    public String getId() {
        return id;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public FavouriteItem getMovieDetails() {
        return movieDetails;
    }
}
