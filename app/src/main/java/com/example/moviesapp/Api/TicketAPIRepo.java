package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.CreateTicketRequest;
import com.example.moviesapp.Domain.CreateTicketResponse;
import com.example.moviesapp.Domain.Ticket;
import com.example.moviesapp.Domain.UpdateSeatRequest;
import com.example.moviesapp.Domain.UpdateSeatResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketAPIRepo {
    private final TicketAPI api;

    public TicketAPIRepo() {
        api = TicketAPIRetrofitClient.getTicketAPI();
    }

    public void updateSeatStatus(String movieName, String showDate, String showTime, int seatIndex,
                                 final OnResultCallback<UpdateSeatResponse> callback) {
        UpdateSeatRequest request = new UpdateSeatRequest(movieName, showDate, showTime, seatIndex);
        Call<UpdateSeatResponse> call = api.updateSeatStatus(request);

        call.enqueue(new Callback<UpdateSeatResponse>() {
            @Override
            public void onResponse(Call<UpdateSeatResponse> call, Response<UpdateSeatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Request failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<UpdateSeatResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void createTicket(String movieName, String showDate, String showTime, List<String> seats, String userId, long totalPrice,
                             final OnResultCallback<CreateTicketResponse> callback) {
        CreateTicketRequest request = new CreateTicketRequest(movieName, showDate, showTime, seats, userId, totalPrice);
        Call<CreateTicketResponse> call = api.createTicket(request);

        call.enqueue(new Callback<CreateTicketResponse>() {
            @Override
            public void onResponse(Call<CreateTicketResponse> call, Response<CreateTicketResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Request failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<CreateTicketResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getTicketsByUser(String userId, final OnResultCallback<List<Ticket>> callback) {
        Call<List<Ticket>> call = api.getTicketsByUser(userId);
        call.enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else if (response.code() == 404) {
                    callback.onSuccess(new ArrayList<>());
                } else {
                    callback.onError(new Exception("Request failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Ticket>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface OnResultCallback<T> {
        void onSuccess(T result);
        void onError(Throwable error);
    }
}