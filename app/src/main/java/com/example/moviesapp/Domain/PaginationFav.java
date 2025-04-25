package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

public class PaginationFav {
    @SerializedName("totalFavourites")
    private int totalFavourites;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("limit")
    private int limit;

    @SerializedName("lastDocId")
    private String lastDocId;

    public int getTotalFavourites() {
        return totalFavourites;
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
