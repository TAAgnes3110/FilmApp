package com.example.moviesapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Domain.Ticket;
import com.example.moviesapp.R;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private List<Ticket> ticketList;

    public TicketAdapter() {
        this.ticketList = new ArrayList<>();
    }

    public void addTicket(Ticket ticket) {
        ticketList.add(ticket);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.movieNameTextView.setText("Phim: " + ticket.getMovieName());
        holder.showDateTextView.setText("Ngày chiếu: " + ticket.getShowDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        holder.showTimeTextView.setText("Giờ chiếu: " + ticket.getShowTime());
        holder.seatsTextView.setText("Ghế: " + ticket.getSeats().toString());
        holder.bookingTimeTextView.setText("Thời gian đặt: " + ticket.getBookingTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView movieNameTextView, showDateTextView, showTimeTextView, seatsTextView, bookingTimeTextView;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            movieNameTextView = itemView.findViewById(R.id.movieNameTextView);
            showDateTextView = itemView.findViewById(R.id.showDateTextView);
            showTimeTextView = itemView.findViewById(R.id.showTimeTextView);
            seatsTextView = itemView.findViewById(R.id.seatsTextView);
            bookingTimeTextView = itemView.findViewById(R.id.bookingTimeTextView);
        }
    }
}