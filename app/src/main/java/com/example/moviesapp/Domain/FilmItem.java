package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

public class FilmItem {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("poster_url")
    private String posterUrl;

    @SerializedName("vote_average")
    private float voteAverage;

    // Getters và Setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public float getVoteAverage() {
        return voteAverage;
    }
}