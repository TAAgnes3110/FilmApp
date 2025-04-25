package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.FilmDetail;
import com.example.moviesapp.Domain.FilmMovieItems;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MovieRepo {
    private MovieApiService apiService;
    public MovieRepo() {
        apiService = ContentFilmMovieRetrofitClient.getClient().create(MovieApiService.class);
    }

    // Gọi API để lấy danh sách phim từ URL endpoint "movies"
    public void getMovies(Callback<List<FilmMovieItems>> callback) {
        Call<List<FilmMovieItems>> call = apiService.getMovies();
        call.enqueue(callback); // Thực hiện yêu cầu bất đồng bộ và trả về kết quả qua callback
    }

    // Gọi API để lấy chi tiết phim từ URL endpoint "movies/{id}"
    public void getMovieDetails(String movieId, Callback<FilmDetail> callback) {
        Call<FilmDetail> call = apiService.getMovieDetails(movieId);
        call.enqueue(callback); // Thực hiện yêu cầu bất đồng bộ và trả về kết quả qua callback
    }
}