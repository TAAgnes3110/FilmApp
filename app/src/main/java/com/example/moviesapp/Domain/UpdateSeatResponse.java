package com.example.moviesapp.Domain;

public class UpdateSeatResponse {
    private String message;
    private String seat_status;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSeat_status() { return seat_status; }
    public void setSeat_status(String seat_status) { this.seat_status = seat_status; }
}