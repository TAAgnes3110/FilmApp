package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavouriteItem {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("genres")
    private List<String> genres;

    @SerializedName("runtime")
    private Integer runtime;

    @SerializedName("vote_average")
    private Float voteAverage;

    @SerializedName("poster_url")
    private String posterUrl;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public List<String> getGenres() {
        return genres;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
