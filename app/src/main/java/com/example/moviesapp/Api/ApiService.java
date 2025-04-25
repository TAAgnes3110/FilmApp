package com.example.moviesapp.Api;

import com.example.moviesapp.Domain.ChangePasswordRequest;
import com.example.moviesapp.Domain.LogoutRequest;
import com.example.moviesapp.Domain.OtpRequest;
import com.example.moviesapp.Domain.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/api/auth/signup")
    Call<ApiResponse> signup(@Body User user);

    @POST("/api/auth/signin")
    Call<ApiResponse> login(@Body User credentials);

    @POST("/api/auth/change-password")
    Call<ApiResponse> changePassword(@Header("Authorization") String token, @Body User payload);

    @POST("/api/secure-auth/change-password-secure")
    Call<ApiResponse> changePasswordSecure(@Header("Authorization") String token, @Body ChangePasswordRequest payload);

    @POST("/api/otp/send-otp")
    Call<ApiResponse> sendOtp(@Body OtpRequest payload);

    @POST("/api/otp/resend-otp")
    Call<ApiResponse> resendOtp(@Body OtpRequest request);

    @POST("/api/otp/verify-otp")
    Call<ApiResponse> verifyOtp(@Body OtpRequest request);

    @POST("/api/otp/send-otp-signup")
    Call<ApiResponse> sendOtpForSignup(@Body OtpRequest payload);

    @GET("/api/users/{userId}")
    Call<ApiResponse> getUserById(@Path("userId") String userId);

    @POST("/api/auth/signout")
    Call<ApiResponse> logout(@Header("Authorization") String authHeader, @Body LogoutRequest request);
}