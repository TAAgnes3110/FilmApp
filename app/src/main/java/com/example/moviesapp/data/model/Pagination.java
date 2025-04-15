package com.example.moviesapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Pagination {
    @SerializedName("totalMovies")
    private int totalMovies;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("limit")
    private int limit;

    @SerializedName("lastDocId")
    private String lastDocId;

    // Getters và Setters
    public int getTotalMovies() {
        return totalMovies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLimit() {
        return limit;
    }

    public String getLastDocId() {
        return lastDocId;
    }
}