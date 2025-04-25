package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cast implements Serializable {
    @SerializedName("name")
    private String name;

    @SerializedName("character")
    private String character;

    @SerializedName("order")
    private Integer order;

    // Getters
    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public Integer getOrder() {
        return order;
    }
}
