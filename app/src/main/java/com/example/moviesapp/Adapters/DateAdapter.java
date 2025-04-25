package com.example.moviesapp.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;

import java.time.LocalDate;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private List<LocalDate> dateList;
    private int selectedPosition = -1;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(LocalDate date);
    }

    public DateAdapter(List<LocalDate> dateList, OnDateClickListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        LocalDate date = dateList.get(position);
        holder.tvDate.setText(String.valueOf(date.getDayOfMonth()));

        // Đổi màu khi được chọn
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.orange)
            );

        } else {
            holder.cardView.setCardBackgroundColor(Color.BLACK); // Màu đen mặc định
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition(); // Lấy vị trí mới nhất

            if (previousSelected != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelected); // Cập nhật lại màu của item trước đó
            }
            notifyItemChanged(selectedPosition); // Cập nhật màu của item mới

            listener.onDateClick(date);
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        CardView cardView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            cardView = (CardView) itemView;
        }
    }
}