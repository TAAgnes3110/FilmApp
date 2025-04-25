package com.example.moviesapp.Domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Crew implements Serializable {
    @SerializedName("name")
    private String name;

    @SerializedName("job")
    private String job;

    @SerializedName("department")
    private String department;

    // Getters
    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }

    public String getDepartment() {
        return department;
    }
}
