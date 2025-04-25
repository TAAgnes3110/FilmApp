package com.example.moviesapp.Domain;

public class UpdateSeatRequest {
    private String movie_name;
    private String show_date;
    private String show_time;
    private int seat_index;

    public UpdateSeatRequest(String movie_name, String show_date, String show_time, int seat_index) {
        this.movie_name = movie_name;
        this.show_date = show_date;
        this.show_time = show_time;
        this.seat_index = seat_index;
    }

    public String getMovie_name() { return movie_name; }
    public void setMovie_name(String movie_name) { this.movie_name = movie_name; }
    public String getShow_date() { return show_date; }
    public void setShow_date(String show_date) { this.show_date = show_date; }
    public String getShow_time() { return show_time; }
    public void setShow_time(String show_time) { this.show_time = show_time; }
    public int getSeat_index() { return seat_index; }
    public void setSeat_index(int seat_index) { this.seat_index = seat_index; }
}