package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.CreateTicketRequest;
import com.example.moviesapp.Domain.CreateTicketResponse;
import com.example.moviesapp.Domain.Ticket;
import com.example.moviesapp.Domain.UpdateSeatRequest;
import com.example.moviesapp.Domain.UpdateSeatResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TicketAPI {
    @POST("theaters/update-seat")
    Call<UpdateSeatResponse> updateSeatStatus(@Body UpdateSeatRequest request);

    @POST("tickets")
    Call<CreateTicketResponse> createTicket(@Body CreateTicketRequest request);

    @GET("tickets/by-user")
    Call<List<Ticket>> getTicketsByUser(@Query("user_id") String userId);
}