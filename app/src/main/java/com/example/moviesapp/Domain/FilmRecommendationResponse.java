package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FilmRecommendationResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<FilmItem> recommendations;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public List<FilmItem> getRecommendations() {
        return recommendations != null ? recommendations : new ArrayList<>();
    }

    // Setters (nếu cần)
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setRecommendations(List<FilmItem> recommendations) {
        this.recommendations = recommendations;
    }
}