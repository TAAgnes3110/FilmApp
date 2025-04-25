package com.example.moviesapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviesapp.Api.ContentRepo;
import com.example.moviesapp.Domain.FilmDetail;
import com.example.moviesapp.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView movieName, movieActors, movieDirector, movieGenre, movieDescription, tvReadMore;
    private WebView webViewTrailer;
    private ProgressBar progressBar;
    private ContentRepo repo;
    private boolean isExpanded = false;
    public ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initViews();

        String movieId = getIntent().getStringExtra("movieId");
        Log.d("MovieDetailActivity", "Received movieId: " + movieId);
        if (movieId == null) {
            movieName.setText("Không tìm thấy ID phim.");
            return;
        }

        repo = new ContentRepo();
        fetchMovieDetails(movieId);
    }

    private void initViews() {
        movieName = findViewById(R.id.movieName);
        movieActors = findViewById(R.id.movieActors);
        movieDirector = findViewById(R.id.movieDirector);
        movieGenre = findViewById(R.id.movieGenre);
        movieDescription = findViewById(R.id.movieDescription);
        tvReadMore = findViewById(R.id.tv_read_more);
        webViewTrailer = findViewById(R.id.webViewTrailer);
        progressBar = findViewById(R.id.progressBar);
        buttonBack = findViewById(R.id.btn_back);

        // Đặt màu vàng cho movieName
        movieName.setTextColor(Color.YELLOW);

        // Cấu hình WebView
        webViewTrailer.getSettings().setJavaScriptEnabled(true);
        webViewTrailer.setWebChromeClient(new WebChromeClient());
        webViewTrailer.setWebViewClient(new WebViewClient());

        // Xử lý sự kiện nhấn "Xem thêm"
        tvReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    movieDescription.setMaxLines(Integer.MAX_VALUE);
                    tvReadMore.setText("Thu gọn");
                    isExpanded = true;
                } else {
                    movieDescription.setMaxLines(3);
                    tvReadMore.setText("Xem thêm");
                    isExpanded = false;
                }
            }
        });

        // Xử lý sự kiện nhấn nút Back
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity để quay về CinemaFragment
            }
        });
    }

    private void fetchMovieDetails(String movieId) {
        progressBar.setVisibility(View.VISIBLE);

        repo.getContentDetails(movieId, new Callback<FilmDetail>() {
            @Override
            public void onResponse(Call<FilmDetail> call, Response<FilmDetail> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    FilmDetail filmDetail = response.body();
                    Log.d("MovieDetailActivity", "Fetched film detail: " + filmDetail.toString());
                    displayMovieDetails(filmDetail);
                } else {
                    Log.e("MovieDetailActivity", "API Response failed. Code: " + response.code() + ", Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("MovieDetailActivity", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("MovieDetailActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    movieName.setText("Không thể tải chi tiết phim.");
                }
            }

            @Override
            public void onFailure(Call<FilmDetail> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("MovieDetailActivity", "API Error: " + t.getMessage());
                movieName.setText("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void displayMovieDetails(FilmDetail filmDetail) {
        // Hiển thị dữ liệu từ API
        movieName.setText(filmDetail.getName() != null ? filmDetail.getName() : "Không có tiêu đề");
        movieGenre.setText(filmDetail.getGenre() != null ? filmDetail.getGenre() : "Không có thể loại");
        movieDirector.setText(filmDetail.getDirector() != null ? filmDetail.getDirector() : "Không có đạo diễn");
        movieActors.setText(filmDetail.getActors() != null ? filmDetail.getActors() : "Không có diễn viên");
        String description = filmDetail.getDescription() != null ? filmDetail.getDescription() : "Không có mô tả";
        movieDescription.setText(description);

        // Debug nội dung
        Log.d("MovieDetailActivity", "Description: " + description);
        Log.d("MovieDetailActivity", "Description length: " + description.length());

        // Kiểm tra số dòng và trạng thái bị cắt của movieDescription
        movieDescription.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Xóa listener để tránh gọi nhiều lần
                movieDescription.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int lineCount = movieDescription.getLineCount();
                boolean isEllipsized = isTextViewEllipsized(movieDescription);
                Log.d("MovieDetailActivity", "Description line count: " + lineCount);
                Log.d("MovieDetailActivity", "Is ellipsized: " + isEllipsized);
                if (lineCount >= 3 && isEllipsized) {
                    tvReadMore.setVisibility(View.VISIBLE);
                }
            }
        });

        // Hiển thị trailer
        if (filmDetail.getTrailer() != null && !filmDetail.getTrailer().isEmpty()) {
            String videoId = extractYouTubeId(filmDetail.getTrailer());
            if (videoId != null) {
                String embedUrl = "https://www.youtube.com/embed/" + videoId;
                String html = "<iframe width=\"100%\" height=\"100%\" src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe>";
                webViewTrailer.loadData(html, "text/html", "utf-8");
            } else {
                webViewTrailer.setVisibility(View.GONE);
                Log.d("MovieDetailActivity", "Invalid trailer URL");
            }
        } else {
            webViewTrailer.setVisibility(View.GONE);
            Log.d("MovieDetailActivity", "No trailer available");
        }
    }

    // Hàm kiểm tra TextView có bị cắt (ellipsized) không
    private boolean isTextViewEllipsized(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            int lineCount = layout.getLineCount();
            if (lineCount > 0) {
                int ellipsisCount = layout.getEllipsisCount(lineCount - 1);
                return ellipsisCount > 0;
            }
        }
        return false;
    }

    private String extractYouTubeId(String url) {
        String videoId = null;
        if (url != null && url.trim().length() > 0) {
            String[] split = url.split("v=");
            if (split.length > 1) {
                videoId = split[1];
                int ampersandIndex = videoId.indexOf('&');
                if (ampersandIndex != -1) {
                    videoId = videoId.substring(0, ampersandIndex);
                }
            }
        }
        return videoId;
    }
}