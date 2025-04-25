package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavouriteResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<FavouriteList> favourites;

    @SerializedName("pagination")
    private PaginationFav pagination;

    public boolean isSuccess() {
        return success;
    }

    public List<FavouriteList> getFavourites() {
        return favourites;
    }

    public PaginationFav getPagination() {
        return pagination;
    }
}

