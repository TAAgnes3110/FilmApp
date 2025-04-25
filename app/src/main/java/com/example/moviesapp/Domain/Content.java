package com.example.moviesapp.Domain;

public class Content {
    private String id;
    private String name;
    private String actors;
    private String director;
    private String genre;
    private String description;
    private String trailer;

    // Constructor
    public Content(String id, String name, String actors, String director, String genre, String description, String trailer) {
        this.id = id;
        this.name = name;
        this.actors = actors;
        this.director = director;
        this.genre = genre;
        this.description = description;
        this.trailer = trailer;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getActors() {
        return actors;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getTrailer() {
        return trailer;
    }
}