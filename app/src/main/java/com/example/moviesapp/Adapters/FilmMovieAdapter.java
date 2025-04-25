package com.example.moviesapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviesapp.Activity.MovieDetailActivity;
import com.example.moviesapp.Domain.FilmMovieItems;
import com.example.moviesapp.R;

import java.util.List;

public class FilmMovieAdapter extends RecyclerView.Adapter<FilmMovieAdapter.FilmMovieViewHolder> {
    private List<FilmMovieItems> filmMovieItems;
    private ViewPager2 viewPager2;
    private Context context;

    public FilmMovieAdapter(List<FilmMovieItems> filmMovieItems, ViewPager2 viewPager2) {
        this.filmMovieItems = filmMovieItems;
        this.viewPager2 = viewPager2;
        this.context = viewPager2.getContext();
    }

    @NonNull
    @Override
    public FilmMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmMovieViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.film_movie_items_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FilmMovieViewHolder holder, int position) {
        FilmMovieItems item = filmMovieItems.get(position);
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())

                .into(holder.imageView);

        // Xử lý nhấp chuột, sử dụng position để lấy movieId
        holder.itemView.setOnClickListener(v -> {
            FilmMovieItems clickedItem = filmMovieItems.get(position);
            String movieId = String.valueOf(clickedItem.getId());
            Log.d("FilmMovieAdapter", "Clicked position: " + position + ", movieId: " + movieId);
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movieId", movieId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filmMovieItems.size();
    }

    static class FilmMovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        FilmMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide2);
        }
    }
}