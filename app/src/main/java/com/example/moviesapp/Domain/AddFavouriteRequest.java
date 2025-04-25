package com.example.moviesapp.Domain;

public class AddFavouriteRequest {
    private String movieId;
    private String movieTitle;

    public AddFavouriteRequest(String movieId, String movieTitle) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
}
