package com.example.moviesapp.presentation.fragment;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moviesapp.Adapters.CategoryClickListener;
import com.example.moviesapp.Adapters.CategoryListAdapters;
import com.example.moviesapp.Adapters.FilmListAdapters;
import com.example.moviesapp.Adapters.SliderAdapters;
import com.example.moviesapp.Api.ApiMovie;
import com.example.moviesapp.data.model.FilmItem;
import com.example.moviesapp.data.model.GenreResponse;
import com.example.moviesapp.data.model.GenresItem;
import com.example.moviesapp.data.model.FilmResponse;
import com.example.moviesapp.data.model.SliderItems;
import com.example.moviesapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CategoryClickListener {
    private RecyclerView.Adapter adapterBestMovies, adapterCategory, adapterListFilmGenre, adapterUpComming;
    private RecyclerView recyclerViewBestMovies, recyclerViewCategory, recyclerViewListFilmGenre, recyclerViewUpComming;
    private ProgressBar progressBar1, progressBar2, progressBar3;
    private EditText editTextSearch;
    private ImageView searchButton; // Thêm ImageView

    private List<FilmItem> movieList = new ArrayList<>();
    private int currentPage = 1;
    private String lastDocId = null;
    private boolean isLoading = false;
    private int totalPages = 0;

    private List<GenresItem> categoryList = new ArrayList<>();
    private int currentPageGenre = 1;
    private String lastDocIdGenre = null;
    private boolean isLoadingGenre = false;
    private int totalPagesGenre = 0;
    private String currentGenre = null;

    private ViewPager2 viewPagerSlider;
    private Handler sliderHandler = new Handler();

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);

        // Setup RecyclerView cho danh sách thể loại
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterCategory = new CategoryListAdapters(categoryList, this);
        recyclerViewCategory.setAdapter(adapterCategory);

        // Setup RecyclerView cho danh sách phim theo thể loại
        recyclerViewListFilmGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterListFilmGenre = new FilmListAdapters(movieList);
        recyclerViewListFilmGenre.setAdapter(adapterListFilmGenre);

        // Thêm listener cuộn cho danh sách phim theo thể loại
        setupRecyclerViewGenreScrollListener();

        // Thêm sự kiện tìm kiếm cho ImageView
        setupSearchListener();

        banner();
        sendRequestBestMovies();
        sendRequestCategory();

        return view;
    }

    private void setupSearchListener() {
        searchButton.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            } else {
                // Chuyển sang MovieSearchResultsFragment và truyền từ khóa
                MovieSearchResultsFragment searchResultsFragment = new MovieSearchResultsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                searchResultsFragment.setArguments(bundle);

                // Sử dụng R.id.fragment_main thay vì R.id.fragment_container
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_main, searchResultsFragment); // Sửa ở đây
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
    private void sendRequestBestMovies() {
        if (progressBar1 != null) {
            progressBar1.setVisibility(View.VISIBLE);
        }
        isLoading = true;

        ApiMovie.apiMovie.getListFilms(currentPage, 10, lastDocId).enqueue(new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (progressBar1 != null) {
                    progressBar1.setVisibility(View.GONE);
                }
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    FilmResponse filmResponse = response.body();
                    if (filmResponse.isSuccess()) {
                        List<FilmItem> newMovies = filmResponse.getMovies();
                        totalPages = filmResponse.getPagination().getTotalPages();
                        lastDocId = filmResponse.getPagination().getLastDocId();

                        movieList.addAll(newMovies);

                        if (currentPage == 1) {
                            adapterBestMovies = new FilmListAdapters(movieList);
                            recyclerViewBestMovies.setAdapter(adapterBestMovies);
                            setupRecyclerViewScrollListener();
                        } else {
                            adapterBestMovies.notifyDataSetChanged();
                        }

                        currentPage++;
                    } else {
                        Log.e("MoviesApp", "API returned success: false");
                    }
                } else {
                    Log.e("MoviesApp", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                if (progressBar1 != null) {
                    progressBar1.setVisibility(View.GONE);
                }
                isLoading = false;
                Log.e("MoviesApp", "API call failed: " + t.getMessage());
            }
        });
    }

    private void setupRecyclerViewScrollListener() {
        recyclerViewBestMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewBestMovies.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && currentPage <= totalPages) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                        sendRequestBestMovies();
                    }
                }
            }
        });
    }

    private void sendRequestCategory() {
        if (progressBar2 != null) {
            progressBar2.setVisibility(View.VISIBLE);
        }

        ApiMovie.apiMovie.getAllGenres().enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (progressBar2 != null) {
                    progressBar2.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<GenresItem> genres = response.body().getGenres();
                    if (genres != null && !genres.isEmpty()) {
                        categoryList.clear();
                        categoryList.addAll(genres);
                        if (adapterCategory != null) {
                            adapterCategory.notifyDataSetChanged();
                            String firstGenreName = genres.get(0).getName();
                            sendRequestListFilmGenre(firstGenreName);
                        } else {
                            Log.e("HomeFragment", "adapterCategory is null");
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
                if (progressBar2 != null) {
                    progressBar2.setVisibility(View.GONE);
                }
                Log.e("HomeFragment", "Failed to fetch genres: " + t.getMessage());
            }
        });
    }

    private void sendRequestListFilmGenre(String genreName) {
        if (!genreName.equals(currentGenre)) {
            currentGenre = genreName;
            currentPageGenre = 1;
            lastDocIdGenre = null;
            movieList.clear();
            if (adapterListFilmGenre != null) {
                adapterListFilmGenre.notifyDataSetChanged();
            }
        }

        if (progressBar2 != null) {
            progressBar2.setVisibility(View.VISIBLE);
        }
        isLoadingGenre = true;

        ApiMovie.apiMovie.getMoviesByGenre(genreName, currentPageGenre, 10, lastDocIdGenre).enqueue(new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (progressBar2 != null) {
                    progressBar2.setVisibility(View.GONE);
                }
                isLoadingGenre = false;

                if (response.isSuccessful() && response.body() != null) {
                    FilmResponse filmResponse = response.body();
                    if (filmResponse.isSuccess()) {
                        List<FilmItem> newMovies = filmResponse.getMovies();
                        totalPagesGenre = filmResponse.getPagination().getTotalPages();
                        lastDocIdGenre = filmResponse.getPagination().getLastDocId();

                        movieList.addAll(newMovies);

                        if (currentPageGenre == 1) {
                            if (adapterListFilmGenre != null) {
                                adapterListFilmGenre.notifyDataSetChanged();
                            } else {
                                adapterListFilmGenre = new FilmListAdapters(movieList);
                                recyclerViewListFilmGenre.setAdapter(adapterListFilmGenre);
                            }
                        } else {
                            if (adapterListFilmGenre != null) {
                                adapterListFilmGenre.notifyItemRangeInserted(movieList.size() - newMovies.size(), newMovies.size());
                            }
                        }

                        currentPageGenre++;
                        Log.d("HomeFragment", "Fetched movies for genre " + genreName + ": " + newMovies.size());
                    } else {
                        Log.e("HomeFragment", "API returned success: false");
                    }
                } else {
                    Log.e("HomeFragment", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                if (progressBar2 != null) {
                    progressBar2.setVisibility(View.GONE);
                }
                isLoadingGenre = false;
                Log.e("HomeFragment", "Failed to fetch movies by genre: " + t.getMessage());
            }
        });
    }

    private void setupRecyclerViewGenreScrollListener() {
        recyclerViewListFilmGenre.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewListFilmGenre.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingGenre && currentPageGenre <= totalPagesGenre) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                        sendRequestListFilmGenre(currentGenre);
                    }
                }
            }
        });
    }

    @Override
    public void onCategoryClick(String genreName) {
        sendRequestListFilmGenre(genreName);
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
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
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
        sliderHandler.removeCallbacks(sliderRunnable);
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void initView(View view) {
        viewPagerSlider = view.findViewById(R.id.viewPagerSlider);
        recyclerViewBestMovies = view.findViewById(R.id.viewBestMovies);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory = view.findViewById(R.id.viewListGenres);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewListFilmGenre = view.findViewById(R.id.viewListFilmsGenre);
        recyclerViewListFilmGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar3 = view.findViewById(R.id.progressBar3);
        editTextSearch = view.findViewById(R.id.editTextText);
        searchButton = view.findViewById(R.id.searchButton); // Khởi tạo ImageView
    }
}