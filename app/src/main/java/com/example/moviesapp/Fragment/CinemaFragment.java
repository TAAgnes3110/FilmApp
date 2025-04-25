package com.example.moviesapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moviesapp.Activity.BookingActivity;
import com.example.moviesapp.Adapters.FilmMovieAdapter;
import com.example.moviesapp.Adapters.SliderAdapters;
import com.example.moviesapp.Api.CinemaFilmRepo;
import com.example.moviesapp.Api.MovieRepo;
import com.example.moviesapp.Domain.FilmMovieItems;
import com.example.moviesapp.Domain.SliderItems;
import com.example.moviesapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CinemaFragment extends Fragment {
    private RecyclerView recyclerViewUpComming;
    private ProgressBar progressBar3;
    private ViewPager2 viewPagerSliderFilm, viewPagerSliderPoster;
    private Handler sliderHandler1 = new Handler();
    private Handler sliderHandler2 = new Handler();
    private TextView nameMovietv;
    private List<FilmMovieItems> filmMovieItems;
    private Button buttonBookTicket;

    public CinemaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("CinemaFragment", "onCreateView() called");
        filmMovieItems = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_cinema, container, false);
        initView(view);
        fetchMovies(); // Gọi API bằng Retrofit
        bannerPoster();
        return view;
    }

    private void fetchMovies() {
        Log.d("BannerFilm", "fetchMovies() called");
        progressBar3.setVisibility(View.VISIBLE);

        MovieRepo repo = new MovieRepo();
        repo.getMovies(new Callback<List<FilmMovieItems>>() {
            @Override
            public void onResponse(Call<List<FilmMovieItems>> call, Response<List<FilmMovieItems>> response) {
                progressBar3.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    filmMovieItems.clear();
                    filmMovieItems.addAll(response.body());
                    Log.d("BannerFilm", "Fetched data size: " + filmMovieItems.size());
                    for (FilmMovieItems item : filmMovieItems) {
                        Log.d("BannerFilm", "Movie ID: " + item.getId() + ", Name: " + item.getName());
                    }
                    setupFilmSlider();
                } else {
                    Log.e("BannerFilm", "API Response failed: " + response.message());
                    nameMovietv.setText("Không thể tải dữ liệu phim. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(Call<List<FilmMovieItems>> call, Throwable t) {
                progressBar3.setVisibility(View.GONE);
                Log.e("BannerFilm", "API Error: " + t.getMessage());
                nameMovietv.setText("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void setupFilmSlider() {
        viewPagerSliderFilm.setAdapter(new FilmMovieAdapter(filmMovieItems, viewPagerSliderFilm));
        viewPagerSliderFilm.setClipToPadding(false);
        viewPagerSliderFilm.setClipChildren(false);
        viewPagerSliderFilm.setOffscreenPageLimit(3);
        viewPagerSliderFilm.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(0));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float absPosition = Math.abs(position);
                if (absPosition >= 1) {
                    page.setAlpha(0.5f);
                    page.setScaleY(0.85f);
                    page.setRotationY(position * -20);
                } else {
                    page.setAlpha(1f - absPosition * 0.3f);
                    page.setScaleY(0.85f + (1 - absPosition) * 0.15f);
                    page.setRotationY(position * -10);
                }
            }
        });
        viewPagerSliderFilm.setPageTransformer(compositePageTransformer);
        viewPagerSliderFilm.setCurrentItem(1);

        if (!filmMovieItems.isEmpty()) {
            nameMovietv.setText(filmMovieItems.get(1).getName());
        }

        viewPagerSliderFilm.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position >= 0 && position < filmMovieItems.size()) {
                    nameMovietv.setText(filmMovieItems.get(position).getName());
                    Log.d("BannerFilm", "Page selected: " + position + ", Title: " + filmMovieItems.get(position).getName());
                }
                sliderHandler1.removeCallbacks(sliderRunnableFilm);
                sliderHandler1.postDelayed(sliderRunnableFilm, 3000);
            }
        });

        // Thiết lập sự kiện cho buttonBookTicket tại đây để đảm bảo dữ liệu đã sẵn sàng
        buttonBookTicket.setOnClickListener(v -> {
            int currentPosition = viewPagerSliderFilm.getCurrentItem();
            if (currentPosition >= 0 && currentPosition < filmMovieItems.size()) {
                String movieName = filmMovieItems.get(currentPosition).getName();
                Intent intent = new Intent(getContext(), BookingActivity.class);
                intent.putExtra("movieName", movieName);
                startActivity(intent);
            } else {
                nameMovietv.setText("Vui lòng chọn một phim để đặt vé.");
            }
        });
    }

    private void bannerPoster() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.giamgia1));
        sliderItems.add(new SliderItems(R.drawable.giamgia2));
        sliderItems.add(new SliderItems(R.drawable.giamgia3));

        viewPagerSliderPoster.setAdapter(new SliderAdapters(sliderItems, viewPagerSliderPoster));
        viewPagerSliderPoster.setClipToPadding(false);
        viewPagerSliderPoster.setOffscreenPageLimit(3);
        viewPagerSliderPoster.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPagerSliderPoster.setPageTransformer(compositePageTransformer);
        viewPagerSliderPoster.setCurrentItem(1);
        viewPagerSliderPoster.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler2.removeCallbacks(sliderRunnablePoster);
                sliderHandler2.postDelayed(sliderRunnablePoster, 3000);
            }
        });
    }

    private Runnable sliderRunnablePoster = new Runnable() {
        @Override
        public void run() {
            if (viewPagerSliderPoster.getAdapter() != null) {
                int itemCount = viewPagerSliderPoster.getAdapter().getItemCount();
                int currentItem = viewPagerSliderPoster.getCurrentItem();
                int nextItem = (currentItem + 1) % itemCount;
                viewPagerSliderPoster.setCurrentItem(nextItem, true);
            }
            sliderHandler2.postDelayed(this, 2500);
        }
    };

    private Runnable sliderRunnableFilm = new Runnable() {
        @Override
        public void run() {
            if (viewPagerSliderFilm.getAdapter() != null) {
                int itemCount = viewPagerSliderFilm.getAdapter().getItemCount();
                int currentItem = viewPagerSliderFilm.getCurrentItem();
                int nextItem = (currentItem + 1) % itemCount;
                viewPagerSliderFilm.setCurrentItem(nextItem, true);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler1.removeCallbacks(sliderRunnableFilm);
        sliderHandler2.removeCallbacks(sliderRunnablePoster);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler2.removeCallbacks(sliderRunnablePoster);
        sliderHandler2.postDelayed(sliderRunnablePoster, 3000);
    }

    private void initView(View view) {
        viewPagerSliderFilm = view.findViewById(R.id.viewPagerSliderFilm);
        viewPagerSliderPoster = view.findViewById(R.id.viewPagerSliderPoster);
        recyclerViewUpComming = view.findViewById(R.id.viewRcmMovies);
        recyclerViewUpComming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        progressBar3 = view.findViewById(R.id.progressBar3);
        nameMovietv = view.findViewById(R.id.nameMovietv);
        buttonBookTicket = view.findViewById(R.id.buttonBookTicket);
    }
}