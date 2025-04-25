package com.example.moviesapp.Domain;

import java.util.List;

public class CreateTicketRequest {
    private String movie_name;
    private String show_date;
    private String show_time;
    private List<String> seats;
    private String user_id;
    private long total_price;

    public CreateTicketRequest(String movie_name, String show_date, String show_time, List<String> seats, String user_id, long total_price) {
        this.movie_name = movie_name;
        this.show_date = show_date;
        this.show_time = show_time;
        this.seats = seats;
        this.user_id = user_id;
        this.total_price = total_price;
    }

    // Getters and setters
    public String getMovie_name() { return movie_name; }
    public void setMovie_name(String movie_name) { this.movie_name = movie_name; }
    public String getShow_date() { return show_date; }
    public void setShow_date(String show_date) { this.show_date = show_date; }
    public String getShow_time() { return show_time; }
    public void setShow_time(String show_time) { this.show_time = show_time; }
    public List<String> getSeats() { return seats; }
    public void setSeats(List<String> seats) { this.seats = seats; }
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    public long getTotal_price() { return total_price; }
    public void setTotal_price(long total_price) { this.total_price = total_price; }
}