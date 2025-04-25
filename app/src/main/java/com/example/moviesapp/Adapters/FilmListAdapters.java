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
import com.example.moviesapp.Domain.FilmItem;
import com.example.moviesapp.R;

import java.util.List;

public class FilmListAdapters extends RecyclerView.Adapter<FilmListAdapters.ViewHolder> {
    private List<FilmItem> items;
    private Context context;

    public FilmListAdapters(List<FilmItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilmItem item = items.get(position);
        holder.titleTxt.setText(item.getTitle());

        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(30));
        Glide.with(context)
                .load(item.getPosterUrl())
                .apply(requestOptions)
                .into(holder.pic);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("movieId", String.valueOf(item.getId())); // Chuyển id thành String
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleFavTxt);
            pic = itemView.findViewById(R.id.picFav);
        }
    }
}