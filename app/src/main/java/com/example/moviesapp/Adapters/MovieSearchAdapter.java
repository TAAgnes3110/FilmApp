package com.example.moviesapp.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.Activity.DetailActivity;
import com.example.moviesapp.Domain.MovieSearch;
import com.example.moviesapp.R;

import java.util.List;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.ViewHolder> {
    private List<MovieSearch> movieSearchList;

    public MovieSearchAdapter(List<MovieSearch> movieSearchList) {
        this.movieSearchList = movieSearchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieSearch movie = movieSearchList.get(position);

        // Log để kiểm tra URL
        Log.d("GlideDebug", "Loading poster URL: " + movie.getPosterUrl());

        // Tải poster_url bằng Glide
        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(30));;

        Glide.with(holder.itemView.getContext())
                .load(movie.getPosterUrl())
                .apply(requestOptions)
                .into(holder.imageViewPoster);

        // Gán các dữ liệu khác
        holder.textViewTitle.setText(movie.getTitle());
        holder.textViewGenres.setText(String.join(", ", movie.getGenres()));
        holder.textViewOverview.setText(movie.getOverview());
        holder.textViewRating.setText(String.valueOf(movie.getVoteAverage()));
        holder.textViewRuntime.setText(movie.getRuntime() + " min");

        // Thêm sự kiện nhấn vào ImageView (poster)
        holder.imageViewPoster.setOnClickListener(v -> {
            // Lấy movieId của bộ phim
            String movieId = movie.getMovieId();
            // Tạo Intent để chuyển sang DetailActivity
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("movieId", movieId); // Truyền movieId qua Intent
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieSearchList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPoster;
        TextView textViewTitle, textViewGenres, textViewOverview, textViewRating, textViewRuntime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.picMovieSearch);
            textViewTitle = itemView.findViewById(R.id.titleMovieSearchTxt);
            textViewGenres = itemView.findViewById(R.id.genresMovieSearch);
            textViewOverview = itemView.findViewById(R.id.overviewMovieSearch);
            textViewRating = itemView.findViewById(R.id.movieStarMovieSearch);
            textViewRuntime = itemView.findViewById(R.id.movieTimeMovieSearch);
        }
    }
}