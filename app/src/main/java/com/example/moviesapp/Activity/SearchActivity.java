package com.example.moviesapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Adapters.FilmFavouriteAdapters;
import com.example.moviesapp.data.model.FavouriteList;
import com.example.moviesapp.data.model.FavouriteResponse;
import com.example.moviesapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FilmFavouriteAdapters adapter;
    private List<FavouriteList> searchList = new ArrayList<>();
    String query="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recylclerViewSearchMovies);
        progressBar = findViewById(R.id.progressBarSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new FilmFavouriteAdapters(searchList);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        query = intent.getStringExtra("searchQuery");
        sendRequestSearch(query, true);
    }

    private void sendRequestSearch(String query, boolean all) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        com.example.moviesapp.Api.ApiMovie.apiMovie.searchMovies(query, all).enqueue(new Callback<FavouriteResponse>() {
            @Override
            public void onResponse(Call<FavouriteResponse> call, Response<FavouriteResponse> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    FavouriteResponse searchResponse = response.body();
                    if (searchResponse.isSuccess()) {
                        List<FavouriteList> movies = searchResponse.getFavourites();
                        searchList.addAll(movies);
                        adapter.notifyDataSetChanged();
                    } else {
//                        Toast.makeText(this, "No movies found", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(this, "Failed to load movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavouriteResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
//                Toast.makeText(this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}