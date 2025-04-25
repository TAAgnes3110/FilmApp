package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MovieDetailsData implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("movie_id")
    private String movieId;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("genres")
    private List<String> genres;

    @SerializedName("vote_average")
    private Float voteAverage;

    @SerializedName("runtime")
    private Integer runtime;

    @SerializedName("poster_url")
    private String posterUrl;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("cast")
    private List<Cast> cast;

    @SerializedName("crew")
    private List<Crew> crew;

    @SerializedName("keywords")
    private List<String> keywords;

    @SerializedName("production_companies")
    private List<String> productionCompanies;

    @SerializedName("production_countries")
    private List<String> productionCountries;

    @SerializedName("popularity")
    private Float popularity;

    @SerializedName("status")
    private String status;

    @SerializedName("original_language")
    private String originalLanguage;

    // Getters
    public String getId() {
        return id;
    }

    public String getMovieId() {
        return movieId;
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

    public Float getVoteAverage() {
        return voteAverage;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getProductionCompanies() {
        return productionCompanies;
    }

    public List<String> getProductionCountries() {
        return productionCountries;
    }

    public Float getPopularity() {
        return popularity;
    }

    public String getStatus() {
        return status;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }
}
