package com.example.moviesapp.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.Api.ApiMovie;
import com.example.moviesapp.Api.ApiMovieRepo;
import com.example.moviesapp.Domain.AddFavouriteRequest;
import com.example.moviesapp.Domain.Cast;
import com.example.moviesapp.Domain.Crew;
import com.example.moviesapp.Domain.FavouriteList;
import com.example.moviesapp.Domain.FavouriteResponse;
import com.example.moviesapp.Domain.MovieDetailsData;
import com.example.moviesapp.Domain.MovieDetailsResponse;
import com.example.moviesapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView movieNameTxt, movieRateTxt, movieTimeTxt, movieOverview, movieGenres, movieDirector, movieActors, movieDate, movieCountry, movieCompany;
    private ImageView picDetail, backImage, favImage;
    private NestedScrollView scrollView;
    private String userId = ""; // Thay bằng Firebase Auth nếu cần
    private ApiMovieRepo apiMovieRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainDetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        userId = getUserIdFromPreferences();

        apiMovieRepo = new ApiMovieRepo();

        // Lấy movieId từ Intent
        String movieId = getIntent().getStringExtra("movieId");
        if (movieId != null) {
            sendRequest(movieId);
            checkFavouriteStatus(movieId); // Kiểm tra trạng thái yêu thích ban đầu
        } else {
            movieNameTxt.setText("No movie ID provided");
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }

        // Sự kiện nhấn nút yêu thích
        favImage.setOnClickListener(v -> {
            String movieTitle = movieNameTxt.getText().toString();
            if (movieId == null || movieTitle.equals("N/A") || movieTitle.isEmpty()) {
                Toast.makeText(DetailActivity.this, "Cannot add movie to favourites", Toast.LENGTH_SHORT).show();
                return;
            }

            AddFavouriteRequest request = new AddFavouriteRequest(movieId, movieTitle);
            // Kiểm tra danh sách yêu thích
            apiMovieRepo.getFavouriteMovies(userId, 1, 100, null, new Callback<FavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<FavouriteResponse> call, @NonNull Response<FavouriteResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean isFavourite = false;
                        FavouriteList matchedFavourite = null;

                        // Kiểm tra phim có trong danh sách yêu thích không
                        for (FavouriteList favourite : response.body().getFavourites()) {
                            if (favourite.getMovieId().equals(movieId)) {
                                isFavourite = true;
                                matchedFavourite = favourite;
                                break;
                            }
                        }

                        if (isFavourite && matchedFavourite != null) {
                            // Phim đã có → hỏi xóa
                            FavouriteList finalMatchedFavourite = matchedFavourite;
                            new AlertDialog.Builder(DetailActivity.this)
                                    .setTitle("Remove Favourite")
                                    .setMessage("This movie is already in your favourites. Do you want to remove it?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        apiMovieRepo.removeFavouriteMovie(userId, finalMatchedFavourite.getMovieId(), new Callback<Void>() {
                                            @Override
                                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(DetailActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                                                    // Gửi broadcast để làm mới danh sách yêu thích
                                                    LocalBroadcastManager.getInstance(DetailActivity.this)
                                                            .sendBroadcast(new Intent("REFRESH_FAVOURITES"));
                                                    // Cập nhật icon yêu thích
//                                                    favImage.setImageResource(R.drawable.ic_favorite_border);
                                                } else {
                                                    Toast.makeText(DetailActivity.this, "Failed to remove favourite", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        } else {
                            // Phim chưa có → thêm
                            apiMovieRepo.addFavouriteMovie(userId, request, new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(DetailActivity.this, "Added to favourites", Toast.LENGTH_SHORT).show();
                                        // Gửi broadcast để làm mới danh sách yêu thích
                                        LocalBroadcastManager.getInstance(DetailActivity.this)
                                                .sendBroadcast(new Intent("REFRESH_FAVOURITES"));
                                        // Cập nhật icon yêu thích
//                                        favImage.setImageResource(R.drawable.ic_favorite);
                                    } else {
                                        Toast.makeText(DetailActivity.this, "Failed to add favourite", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                    Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(DetailActivity.this, "Failed to load favourites", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FavouriteResponse> call, @NonNull Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void checkFavouriteStatus(String movieId) {
        apiMovieRepo.getFavouriteMovies(userId, 1, 100, null, new Callback<FavouriteResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavouriteResponse> call, @NonNull Response<FavouriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavourite = false;
                    for (FavouriteList favourite : response.body().getFavourites()) {
                        if (favourite.getMovieId().equals(movieId)) {
                            isFavourite = true;
                            break;
                        }
                    }
                    // Cập nhật icon yêu thích
//                    favImage.setImageResource(isFavourite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavouriteResponse> call, @NonNull Throwable t) {
                Log.e("DetailActivity", "Failed to check favourite status: " + t.getMessage());
            }
        });
    }

    private void sendRequest(String movieId) {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        apiMovieRepo.getMovieDetails(movieId, new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    MovieDetailsData item = response.body().getData();
                    displayMovieDetails(item);
                } else {
                    movieNameTxt.setText("Failed to load movie details");
                    Log.e("DetailActivity", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                movieNameTxt.setText("Error loading movie details");
                Log.e("DetailActivity", "API call failed: " + t.getMessage());
            }
        });
    }

    private void displayMovieDetails(MovieDetailsData item) {
        movieNameTxt.setText(item.getTitle() != null ? item.getTitle() : "N/A");
        movieRateTxt.setText(item.getVoteAverage() != null ? String.format("%.1f/10", item.getVoteAverage()) : "N/A");
        movieTimeTxt.setText(item.getRuntime() != null ? item.getRuntime() + " min" : "N/A");
        movieOverview.setText(item.getOverview() != null ? item.getOverview() : "N/A");
        movieGenres.setText(item.getGenres() != null ? String.join(", ", item.getGenres()) : "N/A");
        movieDate.setText(item.getReleaseDate() != null ? item.getReleaseDate() : "N/A");
        movieCountry.setText(item.getProductionCountries() != null ? String.join(", ", item.getProductionCountries()) : "N/A");
        movieCompany.setText(item.getProductionCompanies() != null ? String.join(", ", item.getProductionCompanies()) : "N/A");

        // Hiển thị diễn viên (lấy 5 người đầu)
        if (item.getCast() != null && !item.getCast().isEmpty()) {
            StringBuilder castBuilder = new StringBuilder();
            for (int i = 0; i < Math.min(5, item.getCast().size()); i++) {
                Cast cast = item.getCast().get(i);
                castBuilder.append(cast.getName()).append(" (").append(cast.getCharacter()).append("), ");
            }
            movieActors.setText(castBuilder.length() > 0 ? castBuilder.substring(0, castBuilder.length() - 2) : "N/A");
        } else {
            movieActors.setText("N/A");
        }

        // Hiển thị đạo diễn
        if (item.getCrew() != null && !item.getCrew().isEmpty()) {
            StringBuilder directorBuilder = new StringBuilder();
            for (Crew crew : item.getCrew()) {
                if ("Director".equals(crew.getJob())) {
                    directorBuilder.append(crew.getName()).append(", ");
                }
            }
            movieDirector.setText(directorBuilder.length() > 0 ? directorBuilder.substring(0, directorBuilder.length() - 2) : "N/A");
        } else {
            movieDirector.setText("N/A");
        }

        // Load poster
        RequestOptions requestOptions = new RequestOptions().transform(new RoundedCorners(30));
        Glide.with(this)
                .load(item.getPosterUrl())
                .apply(requestOptions)
                .into(picDetail);
    }

    private String getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getString("userId", null); // Trả về null nếu không tìm thấy userId
    }

    private void initView() {
        movieNameTxt = findViewById(R.id.movieNameTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollView2);
        picDetail = findViewById(R.id.picDetail);
        movieRateTxt = findViewById(R.id.movieStar);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieOverview = findViewById(R.id.movieOverview);
        movieActors = findViewById(R.id.movieActors);
        movieDirector = findViewById(R.id.movieDirector);
        movieDate = findViewById(R.id.movieDate);
        movieGenres = findViewById(R.id.movieGenres);
        movieCountry = findViewById(R.id.movieCountry);
        movieCompany = findViewById(R.id.movieCompany);
        favImage = findViewById(R.id.favImage);
        backImage = findViewById(R.id.backImage);
        backImage.setOnClickListener(view -> finish());
    }
}