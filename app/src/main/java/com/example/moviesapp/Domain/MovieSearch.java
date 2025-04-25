package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieSearch {
    @SerializedName("movie_id")
    private String movieId; // Tên biến khớp với "movie_id"

    @SerializedName("title")
    private String title; // Tên biến đã khớp

    @SerializedName("poster_url")
    private String posterUrl; // Tên biến khớp với "poster_url"

    @SerializedName("vote_average")
    private double voteAverage; // Tên biến khớp với "vote_average"

    @SerializedName("overview")
    private String overview; // Tên biến đã khớp

    @SerializedName("genres")
    private List<String> genres; // Tên biến đã khớp

    @SerializedName("runtime")
    private int runtime; // Tên biến đã khớp

    // Constructor
    public MovieSearch(String movieId, String title, String posterUrl, double voteAverage, String overview, List<String> genres, int runtime) {
        this.movieId = movieId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.genres = genres;
        this.runtime = runtime;
    }

    // Getters
    public String getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getRuntime() {
        return runtime;
    }
}