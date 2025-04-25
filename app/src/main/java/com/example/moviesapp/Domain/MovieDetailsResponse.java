package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MovieDetailsResponse implements Serializable {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private MovieDetailsData data;

    public boolean isSuccess() {
        return success;
    }

    public MovieDetailsData getData() {
        return data;
    }
}

