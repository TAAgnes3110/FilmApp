package com.example.moviesapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviesapp.data.model.FilmMovieItems;
import com.example.moviesapp.R;

import java.util.List;

public class FilmMovieAdapter extends RecyclerView.Adapter<FilmMovieAdapter.FilmMovieViewHolder> {
    private List<FilmMovieItems> filmMovieItems;
    private ViewPager2 viewPager2;

    public FilmMovieAdapter(List<FilmMovieItems> filmMovieItems, ViewPager2 viewPager2) {
        this.filmMovieItems = filmMovieItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public FilmMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmMovieViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.film_movie_items_container, // Giả sử bạn đã có layout này
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
    }

    @Override
    public int getItemCount() {
        return filmMovieItems.size();
    }

    static class FilmMovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        FilmMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide2); // Đảm bảo ID này tồn tại trong layout
        }
    }
}