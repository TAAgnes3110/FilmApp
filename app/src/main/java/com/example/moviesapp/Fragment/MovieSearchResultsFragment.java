package com.example.moviesapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Activity.MainActivity;
import com.example.moviesapp.Adapters.MovieSearchAdapter;
import com.example.moviesapp.Api.ApiMovieRepo;
import com.example.moviesapp.Domain.MovieSearch;
import com.example.moviesapp.Domain.MovieSearchResponse;
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

    private ApiMovieRepo apiMovieRepo;
    private static final String TAG = "MovieSearchResults";

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

        apiMovieRepo = new ApiMovieRepo();

        // Lấy từ khóa từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String query = bundle.getString("query");
            if (query != null && !query.isEmpty()) {
                Log.d(TAG, "Query received: " + query);
                searchMovies(query);
            } else {
                Log.e(TAG, "Query is null or empty");
                Toast.makeText(getContext(), "Query rỗng hoặc null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Bundle is null");
            Toast.makeText(getContext(), "Bundle null", Toast.LENGTH_SHORT).show();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment homeFragment = getParentFragmentManager().findFragmentByTag("Home");
                if (homeFragment != null && getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchFragment(homeFragment);
                } else {
                    Log.e(TAG, "HomeFragment not found");
                    Toast.makeText(getContext(), "Không tìm thấy HomeFragment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void updateQuery(String query) {
        if (query != null && !query.isEmpty()) {
            Log.d(TAG, "Updating query: " + query);
            searchMovies(query);
        } else {
            Log.e(TAG, "Updated query is null or empty");
            Toast.makeText(getContext(), "Query rỗng hoặc null", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchMovies(String query) {
        progressBarSearch.setVisibility(View.VISIBLE);
        Log.d(TAG, "Starting search for query: " + query);

        // Nếu dùng emulator, đổi thành http://10.0.2.2:4000/
        apiMovieRepo.searchMovies(query, true, new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                progressBarSearch.setVisibility(View.GONE);
                Log.d(TAG, "API response: code=" + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieSearch> movies = response.body().getData();
                    Log.d(TAG, "Movies data: " + (movies != null ? movies.size() : "null"));
                    if (movies != null && !movies.isEmpty()) {
                        Log.d(TAG, "API returned " + movies.size() + " movies");
                        movieSearchList.clear();
                        movieSearchList.addAll(movies);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "API returned null or empty movie list");
                        Toast.makeText(getContext(), "Không tìm thấy phim", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API error: code=" + response.code() + ", message=" + response.message());
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                progressBarSearch.setVisibility(View.GONE);
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}