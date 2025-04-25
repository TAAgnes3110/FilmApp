package com.example.moviesapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Domain.GenresItem;
import com.example.moviesapp.R;

import java.util.List;

public class CategoryListAdapters extends RecyclerView.Adapter<CategoryListAdapters.ViewHolder> {
    List<GenresItem> items;
    Context context;
    CategoryClickListener categoryClickListener;

    public CategoryListAdapters(List<GenresItem> items, CategoryClickListener categoryClickListener) {
        this.items = items;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public CategoryListAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapters.ViewHolder holder, int position) {
        GenresItem genre = items.get(position);
        holder.tilteTxt.setText(items.get(position).getName());
        holder.itemView.setOnClickListener(view -> {
            if (categoryClickListener != null) {
                categoryClickListener.onCategoryClick(genre.getName()); // Gửi ID của thể loại
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tilteTxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tilteTxt = itemView.findViewById(R.id.genreTxt);
        }
    }
}
