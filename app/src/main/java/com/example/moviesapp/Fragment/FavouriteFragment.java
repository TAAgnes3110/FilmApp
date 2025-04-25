package com.example.moviesapp.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Api.ApiMovie;
import com.example.moviesapp.Adapters.FilmFavouriteAdapters;
import com.example.moviesapp.Api.ApiMovieRepo;
import com.example.moviesapp.Domain.FavouriteList;
import com.example.moviesapp.Domain.FavouriteResponse;
import com.example.moviesapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteFragment extends Fragment {
    private RecyclerView recyclerViewFavourite;
    private ProgressBar progressBar;
    private FilmFavouriteAdapters adapterFavourite;
    private List<FavouriteList> favList = new ArrayList<>();
    private int currentPage = 1;
    private String lastDocId = null;
    private boolean isLoading = false;
    private int totalPages = 0;
    private String userId = ""; // Thay bằng userId thực tế (hoặc lấy từ Firebase Auth)
    private ApiMovieRepo apiMovieRepo;

    public FavouriteFragment() {}

    private final BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("FavouriteFragment", "Received refresh broadcast");
            refreshFavouriteList();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        initView(view);

        apiMovieRepo = new ApiMovieRepo();

        // Đăng ký BroadcastReceiver
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(refreshReceiver, new IntentFilter("REFRESH_FAVOURITES"));

        userId = getUserIdFromPreferences();

        // Tải danh sách ban đầu
        sendRequestFavourite();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy đăng ký BroadcastReceiver
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(refreshReceiver);
    }

    private void refreshFavouriteList() {
        favList.clear();
        adapterFavourite.notifyDataSetChanged();
        currentPage = 1;
        lastDocId = null;
        sendRequestFavourite();
    }

    public void sendRequestFavourite() {
        if (isLoading) return;

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        isLoading = true;

        apiMovieRepo.getFavouriteMovies(userId, currentPage, 10, lastDocId, new Callback<FavouriteResponse>() {
            @Override
            public void onResponse(Call<FavouriteResponse> call, Response<FavouriteResponse> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    FavouriteResponse favouriteResponse = response.body();
                    if (favouriteResponse.isSuccess()) {
                        List<FavouriteList> movies = favouriteResponse.getFavourites();
                        totalPages = favouriteResponse.getPagination().getTotalPages();
                        lastDocId = favouriteResponse.getPagination().getLastDocId();

                        favList.addAll(movies);
                        adapterFavourite.notifyDataSetChanged();

                        currentPage++;
                        Log.d("FavouriteFragment", "Fetched favourite movies: " + movies.size() + ", Total: " + favList.size());
                    } else {
                        Log.e("FavouriteFragment", "API returned success: false");
                    }
                } else {
                    Log.e("FavouriteFragment", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FavouriteResponse> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                isLoading = false;
                Log.e("FavouriteFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    private void setupRecyclerViewScrollListener() {
        recyclerViewFavourite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewFavourite.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && currentPage <= totalPages) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                        sendRequestFavourite();
                    }
                }
            }
        });
    }

    private String getUserIdFromPreferences() {
        if (getActivity() == null) return null;

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        return prefs.getString("userId", null); // Trả về null nếu không tìm thấy userId
    }

    private void initView(View view) {
        recyclerViewFavourite = view.findViewById(R.id.recylclerViewFavouriteMovies);
        progressBar = view.findViewById(R.id.progressBarFav);
        recyclerViewFavourite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterFavourite = new FilmFavouriteAdapters(favList);
        recyclerViewFavourite.setAdapter(adapterFavourite);
        setupRecyclerViewScrollListener();
    }
}