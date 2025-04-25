package com.example.moviesapp.Domain;


public class FilmMovieItems {
    private int id;
    private String name;
    private String image_url;

    public FilmMovieItems() {
    }

    public FilmMovieItems(int id, String name, String image_url) {
        this.id = id;
        this.name = name;
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }
}