package com.example.moviesapp.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Adapters.MovieSearchAdapter;
import com.example.moviesapp.Api.MovieSearchApi;
import com.example.moviesapp.Api.MovieSearchRetrofitClient;
import com.example.moviesapp.data.model.MovieSearch;
import com.example.moviesapp.data.model.MovieSearchResponse;
import com.example.moviesapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieSearchResultsFragment extends Fragment {
    private RecyclerView recyclerViewSearchResults;
    private ProgressBar progressBarSearch;
    private MovieSearchAdapter adapter;
    private List<MovieSearch> movieSearchList;
    private ImageView back;


    public MovieSearchResultsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_search_results, container, false);

        recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults);
        progressBarSearch = view.findViewById(R.id.progressBarSearch);
        back = view.findViewById(R.id.backImage);
        movieSearchList = new ArrayList<>();
        adapter = new MovieSearchAdapter(movieSearchList);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearchResults.setAdapter(adapter);

        // Lấy từ khóa từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String query = bundle.getString("query");
            if (query != null) {
                searchMovies(query);
            }
        }

        return view;
    }

    private void searchMovies(String query) {
        progressBarSearch.setVisibility(View.VISIBLE);

        MovieSearchApi api = MovieSearchRetrofitClient.getClient("http://192.168.1.3:4000/").create(MovieSearchApi.class);
        Call<MovieSearchResponse> call = api.searchMovies(query, true);
        call.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                progressBarSearch.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieSearch> movies = response.body().getData();
                    if (movies != null) {
                        movieSearchList.clear();
                        movieSearchList.addAll(movies);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy phim", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                progressBarSearch.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}