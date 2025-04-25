package com.example.moviesapp.Domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Ticket implements Serializable {
    private String movieName;
    private LocalDate showDate;
    private String showTime;
    private List<String> seats;
    private LocalDateTime bookingTime;

    public Ticket(String movieName, LocalDate showDate, String showTime, List<String> seats, LocalDateTime bookingTime) {
        this.movieName = movieName;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seats = seats;
        this.bookingTime = bookingTime;
    }

    // Getters và Setters
    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
}