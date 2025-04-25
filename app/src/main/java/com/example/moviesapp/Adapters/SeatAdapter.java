package com.example.moviesapp.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Domain.SeatItem;
import com.example.moviesapp.R;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {
    public List<SeatItem> seatList;
    private OnSeatClickListener listener;
    private List<String> selectedSeats; // Danh sách ghế được chọn

    public SeatAdapter(List<SeatItem> seatList, OnSeatClickListener listener) {
        this.seatList = seatList;
        this.listener = listener;
        this.selectedSeats = new ArrayList<>(); // Khởi tạo danh sách ghế được chọn
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        SeatItem seat = seatList.get(position);
        holder.seatTextView.setText(seat.getSeatNumber());

        switch (seat.getStatus()) {
            case 0: // Trống (available)
                holder.cardView.setCardBackgroundColor(Color.BLACK);
                holder.itemView.setEnabled(true);
                break;
            case 1: // Không trống (booked)
                holder.cardView.setCardBackgroundColor(Color.GRAY);
                holder.itemView.setEnabled(false);
                break;
            case 2: // Được chọn (selected)
                holder.cardView.setCardBackgroundColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.orange)
                );
                holder.itemView.setEnabled(true);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (seat.getStatus() == 0) {
                seat.setStatus(2); // Chuyển từ trống sang được chọn
                selectedSeats.add(seat.getSeatNumber()); // Thêm vào danh sách ghế được chọn
                Log.d("SeatAdapter", "Added seat: " + seat.getSeatNumber() + ", Selected seats: " + selectedSeats.toString());
                notifyItemChanged(position);
                listener.onSeatClick(seat.getSeatNumber());
            } else if (seat.getStatus() == 2) {
                seat.setStatus(0); // Hủy chọn, quay về trống
                selectedSeats.remove(seat.getSeatNumber()); // Xóa khỏi danh sách ghế được chọn
                Log.d("SeatAdapter", "Removed seat: " + seat.getSeatNumber() + ", Selected seats: " + selectedSeats.toString());
                notifyItemChanged(position);
                listener.onSeatClick(seat.getSeatNumber());
            }
        });
    }

    @Override
    public int getItemCount() {
        return seatList != null ? seatList.size() : 0;
    }
    // Phương thức để lấy danh sách ghế được chọn
    public List<String> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    // Phương thức để cập nhật trạng thái ghế sau khi thanh toán
    public void confirmBooking() {
        for (SeatItem seat : seatList) {
            if (seat.getStatus() == 2) {
                seat.setStatus(1); // Chuyển từ "được chọn" sang "đã đặt"
            }
        }
        selectedSeats.clear(); // Xóa danh sách ghế được chọn
        notifyDataSetChanged(); // Cập nhật toàn bộ RecyclerView
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView seatTextView;
        CardView cardView;

        SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            seatTextView = itemView.findViewById(R.id.seatTextView);
            cardView = (CardView) itemView;
        }
    }

    public interface OnSeatClickListener {
        void onSeatClick(String seat);
    }
}