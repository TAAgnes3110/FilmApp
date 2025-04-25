package com.example.moviesapp.Domain;

import java.util.List;

public class MovieSearchResponse {
    private List<MovieSearch> data;

    public List<MovieSearch> getData() {
        return data;
    }

    public void setData(List<MovieSearch> data) {
        this.data = data;
    }
}