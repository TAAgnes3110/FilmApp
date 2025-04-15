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

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
    private List<String> timeList;
    private int selectedPosition = -1; // Theo dõi vị trí được chọn
    private OnTimeClickListener listener;

    public interface OnTimeClickListener {
        void onTimeClick(String time);
    }

    public TimeAdapter(List<String> timeList, OnTimeClickListener listener) {
        this.timeList = timeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        String time = timeList.get(position);
        holder.timeTextView.setText(time);

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

            listener.onTimeClick(time);
        });
    }

    @Override
    public int getItemCount() {
        return timeList != null ? timeList.size() : 0;
    }

    static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        CardView cardView;

        TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.tvTime);
            cardView = (CardView) itemView; // ItemView chính là CardView
        }
    }
}