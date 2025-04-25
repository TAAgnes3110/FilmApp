package com.example.moviesapp.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moviesapp.Activity.MainActivity;
import com.example.moviesapp.Adapters.CategoryClickListener;
import com.example.moviesapp.Adapters.CategoryListAdapters;
import com.example.moviesapp.Adapters.FilmListAdapters;
import com.example.moviesapp.Adapters.PaginationAdapter;
import com.example.moviesapp.Adapters.SliderAdapters;
import com.example.moviesapp.Api.ApiMovieRepo;
import com.example.moviesapp.Api.RecommendationRequest;
import com.example.moviesapp.Domain.FilmItem;
import com.example.moviesapp.Domain.FilmRecommendationResponse;
import com.example.moviesapp.Domain.GenreResponse;
import com.example.moviesapp.Domain.GenresItem;
import com.example.moviesapp.Domain.FilmResponse;
import com.example.moviesapp.Domain.SliderItems;
import com.example.moviesapp.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CategoryClickListener {
    private FilmListAdapters adapterBestMovies, adapterListFilmGenre, adapterRecommendation;
    private CategoryListAdapters adapterCategory;
    private PaginationAdapter adapterPaginationBestMovies, adapterPaginationGenreMovies;
    private RecyclerView recyclerViewBestMovies, recyclerViewCategory, recyclerViewListFilmGenre, recyclerViewRecommendation;
    private RecyclerView recyclerViewPaginationBestMovies, recyclerViewPaginationGenreMovies;
    private ProgressBar progressBar1, progressBar2, progressBar3;
    private EditText editTextSearch;
    private ImageView searchImage;

    // Best Movies
    private List<FilmItem> movieListFull = new ArrayList<>();
    private List<FilmItem> movieListDisplayed = new ArrayList<>();
    private int currentPageBestMovies = 1;
    private int currentDisplayPageBestMovies = 1;
    private String lastDocIdBestMovies = null;
    private boolean isLoadingBestMovies = false;
    private boolean hasReachedEndBestMovies = false; // Biến để theo dõi xem đã tải hết phim chưa
    private final int PAGE_SIZE = 10;
    private final int FETCH_SIZE = 100;
    private final int PRELOAD_THRESHOLD = 2; // Tải thêm khi còn 2 trang chưa hiển thị

    // Genre Movies
    private List<FilmItem> genreMovieListFull = new ArrayList<>();
    private List<FilmItem> genreMovieListDisplayed = new ArrayList<>();
    private int currentPageGenre = 1;
    private int currentDisplayPageGenre = 1;
    private String lastDocIdGenre = null;
    private boolean isLoadingGenre = false;
    private boolean hasReachedEndGenreMovies = false; // Biến để theo dõi xem đã tải hết phim chưa
    private String currentGenre = null;

    private List<GenresItem> categoryList = new ArrayList<>();
    private List<FilmItem> movieListRecommend = new ArrayList<>();
    private ApiMovieRepo apiMovieRepo;

    private ViewPager2 viewPagerSlider;
    private Handler sliderHandler = new Handler();

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);

        apiMovieRepo = new ApiMovieRepo();

        banner();

        if (movieListFull.isEmpty()) {
            fetchBestMovies();
        } else {
            displayBestMoviesPage(currentDisplayPageBestMovies);
            recyclerViewBestMovies.setVisibility(View.VISIBLE);
            updatePaginationBestMovies();
        }

        if (categoryList.isEmpty()) {
            sendRequestCategory();
        } else {
            recyclerViewCategory.setVisibility(View.VISIBLE);
            if (!genreMovieListFull.isEmpty()) {
                displayGenreMoviesPage(currentDisplayPageGenre);
                recyclerViewListFilmGenre.setVisibility(View.VISIBLE);
                updatePaginationGenreMovies();
            }
        }

        if (movieListRecommend.isEmpty()) {
            sendRequestRecommend();
        } else {
            recyclerViewRecommendation.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void setupSearchListener() {
        searchImage.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("HomeFragment", "Search query: " + query);
                Fragment searchFragment = getParentFragmentManager().findFragmentByTag("Search");
                if (searchFragment != null && searchFragment instanceof MovieSearchResultsFragment && getActivity() instanceof MainActivity) {
                    ((MovieSearchResultsFragment) searchFragment).updateQuery(query);
                    ((MainActivity) getActivity()).switchFragment(searchFragment);
                } else {
                    MovieSearchResultsFragment newSearchFragment = new MovieSearchResultsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("query", query);
                    newSearchFragment.setArguments(bundle);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).switchFragment(newSearchFragment);
                    }
                }
            }
        });
    }

    private void fetchBestMovies() {
        if (isLoadingBestMovies || hasReachedEndBestMovies) return; // Dừng nếu đang tải hoặc đã tải hết
        if (progressBar1 != null) progressBar1.setVisibility(View.VISIBLE);
        isLoadingBestMovies = true;

        Log.d("HomeFragment", "Fetching best movies, page=" + currentPageBestMovies);

        apiMovieRepo.getListFilms(currentPageBestMovies, FETCH_SIZE, lastDocIdBestMovies, new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (progressBar1 != null) progressBar1.setVisibility(View.GONE);
                isLoadingBestMovies = false;

                if (response.isSuccessful() && response.body() != null) {
                    FilmResponse filmResponse = response.body();
                    if (filmResponse.isSuccess()) {
                        List<FilmItem> newMovies = filmResponse.getMovies();
                        if (newMovies == null || newMovies.isEmpty()) {
                            Log.w("HomeFragment", "No new movies returned for page " + currentPageBestMovies + ", stopping fetch");
                            hasReachedEndBestMovies = true; // Đánh dấu đã tải hết
                            return;
                        }
                        lastDocIdBestMovies = filmResponse.getPagination().getLastDocId();
                        movieListFull.addAll(newMovies);
                        currentPageBestMovies++;

                        displayBestMoviesPage(currentDisplayPageBestMovies);
                        recyclerViewBestMovies.setVisibility(View.VISIBLE);
                        updatePaginationBestMovies();

                        Log.d("HomeFragment", "Fetched " + newMovies.size() + " movies, total cached: " + movieListFull.size());
                    } else {
                        Log.e("HomeFragment", "API returned success: false");
                    }
                } else {
                    Log.e("HomeFragment", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                if (progressBar1 != null) progressBar1.setVisibility(View.GONE);
                isLoadingBestMovies = false;
                Log.e("HomeFragment", "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi tải phim, đang thử lại...", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> fetchBestMovies(), 3000);
            }
        });
    }

    private void displayBestMoviesPage(int page) {
        currentDisplayPageBestMovies = page;
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, movieListFull.size());

        if (startIndex >= movieListFull.size()) return;

        movieListDisplayed.clear();
        movieListDisplayed.addAll(movieListFull.subList(startIndex, endIndex));
        adapterBestMovies.notifyDataSetChanged();

        updatePaginationBestMovies();

        // Kiểm tra xem có cần tải thêm phim không
        int totalPagesCached = (int) Math.ceil((double) movieListFull.size() / PAGE_SIZE);
        if (page >= totalPagesCached - PRELOAD_THRESHOLD && !isLoadingBestMovies && !hasReachedEndBestMovies) {
            fetchBestMovies();
        }

        Log.d("HomeFragment", "Displayed page " + page + " for Best Movies, items: " + movieListDisplayed.size());
    }

    private void updatePaginationBestMovies() {
        int totalPages = (int) Math.ceil((double) movieListFull.size() / PAGE_SIZE);
        adapterPaginationBestMovies.setPages(Math.max(totalPages, 1), currentDisplayPageBestMovies);
    }

    private void sendRequestCategory() {
        if (!categoryList.isEmpty()) {
            recyclerViewCategory.setVisibility(View.VISIBLE);
            adapterCategory.notifyDataSetChanged();
            return;
        }
        if (progressBar2 != null) progressBar2.setVisibility(View.VISIBLE);

        apiMovieRepo.getAllGenres(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (progressBar2 != null) progressBar2.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<GenresItem> genres = response.body().getGenres();
                    if (genres != null && !genres.isEmpty()) {
                        categoryList.clear();
                        for (GenresItem genre : genres) {
                            if (genre.getName() != null && !genre.getName().isEmpty()) {
                                categoryList.add(genre);
                            }
                        }
                        adapterCategory.notifyDataSetChanged();
                        if (!categoryList.isEmpty()) {
                            String firstGenreName = categoryList.get(0).getName();
                            sendRequestListFilmGenre(firstGenreName);
                        }
                    } else {
                        Log.e("HomeFragment", "No genres returned");
                    }
                } else {
                    Log.e("HomeFragment", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                if (progressBar2 != null) progressBar2.setVisibility(View.GONE);
                Log.e("HomeFragment", "Failed to fetch genres: " + t.getMessage());
                recyclerViewCategory.setVisibility(View.GONE);
            }
        });
    }

    private void sendRequestListFilmGenre(String genreName) {
        if (genreName == null || genreName.isEmpty()) {
            Log.e("HomeFragment", "Genre name is null or empty");
            Toast.makeText(getContext(), "Thể loại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentGenre == null || !genreName.equals(currentGenre)) {
            currentGenre = genreName;
            currentPageGenre = 1;
            currentDisplayPageGenre = 1;
            lastDocIdGenre = null;
            genreMovieListFull.clear();
            genreMovieListDisplayed.clear();
            hasReachedEndGenreMovies = false; // Reset trạng thái khi đổi thể loại
            if (adapterListFilmGenre != null) {
                adapterListFilmGenre.notifyDataSetChanged();
            }
            fetchGenreMovies();
        } else {
            displayGenreMoviesPage(currentDisplayPageGenre);
        }
    }

    private void fetchGenreMovies() {
        if (isLoadingGenre || hasReachedEndGenreMovies) return; // Dừng nếu đang tải hoặc đã tải hết
        if (progressBar2 != null) progressBar2.setVisibility(View.VISIBLE);
        isLoadingGenre = true;

        Log.d("HomeFragment", "Fetching movies for genre: " + currentGenre + ", page: " + currentPageGenre);

        apiMovieRepo.getMoviesByGenre(currentGenre, currentPageGenre, FETCH_SIZE, lastDocIdGenre, new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (progressBar2 != null) progressBar2.setVisibility(View.GONE);
                isLoadingGenre = false;

                if (response.isSuccessful() && response.body() != null) {
                    FilmResponse filmResponse = response.body();
                    if (filmResponse.isSuccess()) {
                        List<FilmItem> newMovies = filmResponse.getMovies();
                        if (newMovies == null || newMovies.isEmpty()) {
                            Log.w("HomeFragment", "No movies returned for genre " + currentGenre + ", page " + currentPageGenre + ", stopping fetch");
                            hasReachedEndGenreMovies = true; // Đánh dấu đã tải hết
                            return;
                        }
                        lastDocIdGenre = filmResponse.getPagination().getLastDocId();
                        genreMovieListFull.addAll(newMovies);
                        currentPageGenre++;

                        displayGenreMoviesPage(currentDisplayPageGenre);
                        recyclerViewListFilmGenre.setVisibility(View.VISIBLE);
                        updatePaginationGenreMovies();

                        Log.d("HomeFragment", "Fetched " + newMovies.size() + " genre movies, total cached: " + genreMovieListFull.size());
                    } else {
                        Log.e("HomeFragment", "API returned success: false for genre " + currentGenre);
                    }
                } else {
                    Log.e("HomeFragment", "Response failed for genre " + currentGenre + ": " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                if (progressBar2 != null) progressBar2.setVisibility(View.GONE);
                isLoadingGenre = false;
                Log.e("HomeFragment", "Failed to fetch movies by genre " + currentGenre + ": " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi tải phim theo thể loại, thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayGenreMoviesPage(int page) {
        currentDisplayPageGenre = page;
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, genreMovieListFull.size());

        if (startIndex >= genreMovieListFull.size()) return;

        genreMovieListDisplayed.clear();
        genreMovieListDisplayed.addAll(genreMovieListFull.subList(startIndex, endIndex));
        adapterListFilmGenre.notifyDataSetChanged();

        updatePaginationGenreMovies();

        // Kiểm tra xem có cần tải thêm phim không
        int totalPagesCached = (int) Math.ceil((double) genreMovieListFull.size() / PAGE_SIZE);
        if (page >= totalPagesCached - PRELOAD_THRESHOLD && !isLoadingGenre && !hasReachedEndGenreMovies) {
            fetchGenreMovies();
        }

        Log.d("HomeFragment", "Displayed page " + page + " for Genre Movies, items: " + genreMovieListDisplayed.size());
    }

    private void updatePaginationGenreMovies() {
        int totalPages = (int) Math.ceil((double) genreMovieListFull.size() / PAGE_SIZE);
        adapterPaginationGenreMovies.setPages(Math.max(totalPages, 1), currentDisplayPageGenre);
    }

    @Override
    public void onCategoryClick(String genreName) {
        sendRequestListFilmGenre(genreName);
    }

    private void sendRequestRecommend() {
        if (!movieListRecommend.isEmpty()) {
            recyclerViewRecommendation.setVisibility(View.VISIBLE);
            adapterRecommendation.notifyDataSetChanged();
            return;
        }
        progressBar3.setVisibility(View.VISIBLE);

        RecommendationRequest request = new RecommendationRequest(Integer.valueOf(getUserIdFromPreferences()), 10);

        apiMovieRepo.getRecommendations(request).enqueue(new Callback<FilmRecommendationResponse>() {
            @Override
            public void onResponse(Call<FilmRecommendationResponse> call, Response<FilmRecommendationResponse> response) {
                progressBar3.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    FilmRecommendationResponse recommendationResponse = response.body();
                    if (recommendationResponse.isSuccess()) {
                        List<FilmItem> recommendations = recommendationResponse.getRecommendations();
                        movieListRecommend.clear();
                        if (!recommendations.isEmpty()) {
                            movieListRecommend.addAll(recommendations);
                            recyclerViewRecommendation.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewRecommendation.setVisibility(View.GONE);
                        }
                        adapterRecommendation.notifyDataSetChanged();
                    } else {
                        recyclerViewRecommendation.setVisibility(View.GONE);
                    }
                } else {
                    recyclerViewRecommendation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<FilmRecommendationResponse> call, Throwable t) {
                progressBar3.setVisibility(View.GONE);
                recyclerViewRecommendation.setVisibility(View.GONE);
            }
        });
    }

    private void banner() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide));
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide3));

        viewPagerSlider.setAdapter(new SliderAdapters(sliderItems, viewPagerSlider));
        viewPagerSlider.setClipToPadding(false);
        viewPagerSlider.setClipChildren(false);
        viewPagerSlider.setOffscreenPageLimit(3);
        viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPagerSlider.setPageTransformer(compositePageTransformer);
        viewPagerSlider.setCurrentItem(1);
        viewPagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerSlider.getAdapter() != null) {
                int itemCount = viewPagerSlider.getAdapter().getItemCount();
                int currentItem = viewPagerSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % itemCount;
                viewPagerSlider.setCurrentItem(nextItem, true);
            }
            sliderHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private String getUserIdFromPreferences() {
        if (getActivity() == null) return null;
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    private void initView(View view) {
        viewPagerSlider = view.findViewById(R.id.viewPagerSlider);
        recyclerViewBestMovies = view.findViewById(R.id.viewBestMovies);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory = view.findViewById(R.id.viewListGenres);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewListFilmGenre = view.findViewById(R.id.viewListFilmsGenre);
        recyclerViewListFilmGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecommendation = view.findViewById(R.id.viewRcmMovies);
        recyclerViewRecommendation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPaginationBestMovies = view.findViewById(R.id.paginationBestMovies);
        recyclerViewPaginationBestMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPaginationGenreMovies = view.findViewById(R.id.paginationGenreMovies);
        recyclerViewPaginationGenreMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar3 = view.findViewById(R.id.progressBar3);
        editTextSearch = view.findViewById(R.id.editTextText);
        searchImage = view.findViewById(R.id.searchImage);

        adapterBestMovies = new FilmListAdapters(movieListDisplayed);
        recyclerViewBestMovies.setAdapter(adapterBestMovies);

        adapterCategory = new CategoryListAdapters(categoryList, this);
        recyclerViewCategory.setAdapter(adapterCategory);

        adapterListFilmGenre = new FilmListAdapters(genreMovieListDisplayed);
        recyclerViewListFilmGenre.setAdapter(adapterListFilmGenre);

        adapterPaginationBestMovies = new PaginationAdapter(page -> displayBestMoviesPage(page));
        recyclerViewPaginationBestMovies.setAdapter(adapterPaginationBestMovies);

        adapterPaginationGenreMovies = new PaginationAdapter(page -> displayGenreMoviesPage(page));
        recyclerViewPaginationGenreMovies.setAdapter(adapterPaginationGenreMovies);

        setupSearchListener();

        adapterRecommendation = new FilmListAdapters(movieListRecommend);
        recyclerViewRecommendation.setAdapter(adapterRecommendation);
    }
}