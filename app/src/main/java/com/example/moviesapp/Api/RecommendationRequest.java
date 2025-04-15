package com.example.moviesapp.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecommendationRequest {
    @SerializedName("user_id")
    @Expose
    private int user_id;

    @SerializedName("ratings")
    @Expose
    private List<Integer> ratings;

    @SerializedName("favorite_movies")
    @Expose
    private List<String> favorite_movies;

    @SerializedName("n_recommendations")
    @Expose
    private Integer n_recommendations;

    // Constructor đầy đủ
    public RecommendationRequest(int user_id, List<Integer> ratings, List<String> favorite_movies, Integer n_recommendations) {
        if (user_id <= 0) {
            throw new IllegalArgumentException("user_id must be positive");
        }
        if (n_recommendations != null && n_recommendations <= 0) {
            throw new IllegalArgumentException("n_recommendations must be positive");
        }
        this.user_id = user_id;
        this.ratings = ratings;
        this.favorite_movies = favorite_movies;
        this.n_recommendations = (n_recommendations != null) ? n_recommendations : 5;
    }

    // Constructor tối thiểu cho trường hợp không cần ratings và favorite_movies
    public RecommendationRequest(int user_id, Integer n_recommendations) {
        this(user_id, null, null, n_recommendations);
    }

    // Getters
    public int getUser_id() {
        return user_id;
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public List<String> getFavorite_movies() {
        return favorite_movies;
    }

    public Integer getN_recommendations() {
        return n_recommendations;
    }
}