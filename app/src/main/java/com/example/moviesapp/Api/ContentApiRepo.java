package com.example.moviesapp.Api;

import android.util.Log;
import com.example.moviesapp.Domain.Content;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContentApiRepo {
    private final ContentApiService apiService;

    public ContentApiRepo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.3:6000/") // Địa chỉ server Node.js
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ContentApiService.class);
    }

    public interface OnResultCallback<T> {
        void onSuccess(T result);
        void onError(Throwable error);
    }

    public void getContents(OnResultCallback<List<Content>> callback) {
        Call<List<Content>> call = apiService.getContents();
        call.enqueue(new Callback<List<Content>>() {
            @Override
            public void onResponse(Call<List<Content>> call, Response<List<Content>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Content>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getContentById(String id, OnResultCallback<Content> callback) {
        Call<Content> call = apiService.getContentById(id);
        call.enqueue(new Callback<Content>() {
            @Override
            public void onResponse(Call<Content> call, Response<Content> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void updateContent(String id, Content content, OnResultCallback<Void> callback) {
        Call<Void> call = apiService.updateContent(id, content);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}