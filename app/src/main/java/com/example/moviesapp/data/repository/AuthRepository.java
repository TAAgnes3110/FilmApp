package com.example.moviesapp.data.repository;

import android.app.Activity;
import android.util.Log;

import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.data.model.OtpRequest;
import com.example.moviesapp.data.model.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";

    public interface ApiCallback {
        void onSuccess(int status, String message);
        void onFailure(String error);
    }

    public interface LoginApiCallback {
        void onSuccess(int status, User user, String message);
        void onFailure(String error);
    }

    public void sendOtp(Activity activity, OtpRequest request, ApiCallback callback) {
        Call<ApiResponse> call = RetrofitClient.getApiService(activity).sendOtp(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Send OTP response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    callback.onSuccess(apiResponse.getStatus(), apiResponse.getMessage());
                } else {
                    callback.onFailure("Failed to send OTP. (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void verifyOtp(Activity activity, OtpRequest request, ApiCallback callback) {
        Call<ApiResponse> call = RetrofitClient.getApiService(activity).verifyOtp(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Verify OTP response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    callback.onSuccess(apiResponse.getStatus(), apiResponse.getMessage());
                } else {
                    callback.onFailure("OTP verification failed. (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void sendOtpForSignup(Activity activity, OtpRequest request, ApiCallback callback) {
        Call<ApiResponse> call = RetrofitClient.getApiService(activity).sendOtpForSignup(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Send OTP for signup response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    callback.onSuccess(apiResponse.getStatus(), apiResponse.getMessage());
                } else {
                    callback.onFailure("Failed to send OTP.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void signup(Activity activity, User user, ApiCallback callback) {
        Call<ApiResponse> call = RetrofitClient.getApiService(activity).signup(user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Signup response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    callback.onSuccess(apiResponse.getStatus(), apiResponse.getMessage());
                } else {
                    callback.onFailure("Signup failed.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void login(Activity activity, User credentials, LoginApiCallback callback) {
        Call<ApiResponse> call = RetrofitClient.getApiService(activity).login(credentials);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Login response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    User user = extractUserFromResponse(apiResponse.getData());
                    callback.onSuccess(apiResponse.getStatus(), user, apiResponse.getMessage());
                } else {
                    callback.onFailure("Login failed.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void changePassword(Activity activity, User payload, ApiCallback callback) {
        Call<ApiResponse> call = RetrofitClient.getApiService(activity).changePassword(null, payload);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Change password response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Status: " + apiResponse.getStatus() + ", Message: " + apiResponse.getMessage());
                    callback.onSuccess(apiResponse.getStatus(), apiResponse.getMessage());
                } else {
                    callback.onFailure("Password change failed: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    private User extractUserFromResponse(Object data) {
        if (data == null) return null;
        try {
            Gson gson = new Gson();
            String jsonData = gson.toJson(data);
            return gson.fromJson(jsonData, User.class);
        } catch (Exception e) {
            return null;
        }
    }
}