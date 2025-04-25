package com.example.moviesapp.Domain;

public class CreateTicketResponse {
    private String message;
    private Ticket ticket;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
}