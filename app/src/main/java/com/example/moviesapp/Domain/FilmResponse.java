package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilmResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<FilmItem> movies; // Giả sử FilmItem là class đại diện cho mỗi phim

    @SerializedName("pagination")
    private Pagination pagination;

    // Getters và Setters
    public boolean isSuccess() {
        return success;
    }

    public List<FilmItem> getMovies() {
        return movies;
    }

    public Pagination getPagination() {
        return pagination;
    }
}