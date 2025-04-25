package com.example.moviesapp.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.moviesapp.Domain.FavouriteList;
import com.example.moviesapp.R;

import java.util.List;

public class FilmFavouriteAdapters extends RecyclerView.Adapter<FilmFavouriteAdapters.ViewHolder> {
    private List<FavouriteList> items;
    private Context context;

    public FilmFavouriteAdapters(List<FavouriteList> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FilmFavouriteAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_favourite, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmFavouriteAdapters.ViewHolder holder, int position) {
        FavouriteList item = items.get(position);
        if (item.getMovieDetails() != null) {
            holder.titleTxt.setText(item.getMovieDetails().getTitle());
            holder.overviewTxt.setText(item.getMovieDetails().getOverview());
            holder.genresTxt.setText(String.join(", ",item.getMovieDetails().getGenres()));
            holder.star.setText(item.getMovieDetails().getVoteAverage() != null ? String.format("%.1f/10", item.getMovieDetails().getVoteAverage()) : "N/A");
            holder.time.setText(item.getMovieDetails().getRuntime() != null ? item.getMovieDetails().getRuntime() + " min" : "N/A");
            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CenterCrop(), new RoundedCorners(30));
            Glide.with(context)
                    .load(item.getMovieDetails().getPosterUrl())
                    .apply(requestOptions)
                    .into(holder.picFav);

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("movieId", String.valueOf(item.getMovieId())); // Chuyển id thành String
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, overviewTxt, genresTxt, star, time;
        ImageView picFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleFavTxt);
            overviewTxt = itemView.findViewById(R.id.overviewFav);
            genresTxt = itemView.findViewById(R.id.genresFav);
            picFav = itemView.findViewById(R.id.picFav);
            star = itemView.findViewById(R.id.movieStarFav);
            time = itemView.findViewById(R.id.movieTimeFav);
        }
    }
}