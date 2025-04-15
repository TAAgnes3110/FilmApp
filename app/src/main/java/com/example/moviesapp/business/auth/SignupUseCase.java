package com.example.moviesapp.business.auth;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.Log;

import com.example.moviesapp.data.repository.AuthRepository;
import com.example.moviesapp.data.model.OtpRequest;
import com.example.moviesapp.data.model.User;

public class SignupUseCase {
    private AuthRepository authRepository = new AuthRepository();
    private static final String TAG = "SignupUseCase";

    public interface SignupCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void handleSignup(User user, Activity activity, SignupCallback callback) {
        if (!validateInput(user.getEmail(), user.getPassword(), user.getFullName(), user.getPhoneNumber(), user.getUserName())) {
            callback.onError("Invalid input. Please check all fields.");
            return;
        }

        OtpRequest otpRequest = new OtpRequest(user.getEmail(), "signup", true);
        authRepository.sendOtpForSignup(activity, otpRequest, new AuthRepository.ApiCallback() {
            @Override
            public void onSuccess(int status, String message) {
                Log.d(TAG, "Send OTP success with status: " + status);
                if (status == 200) {
                    callback.onSuccess("OTP has been sent to your email!");
                } else {
                    callback.onError(message != null ? message : "Failed to send OTP.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Send OTP failed: " + error);
                callback.onError("Network error: " + error);
            }
        });
    }

    private boolean validateInput(String email, String password, String fullName, String phone, String username) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(fullName) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(username)) {
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        if (!phone.matches("\\d{10,11}")) {
            return false;
        }
        return true;
    }
}