package com.example.moviesapp.business.auth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.moviesapp.data.repository.AuthRepository;
import com.example.moviesapp.data.model.User;

public class LoginUseCase {
    private AuthRepository authRepository = new AuthRepository();
    private static final String TAG = "LoginUseCase";

    public interface LoginCallback {
        void onSuccess(User user, String message);
        void onError(String message);
    }

    public void attemptLogin(String username, String password, Activity activity, LoginCallback callback) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            callback.onError("Please fill in all information.");
            return;
        }

        User credentials = new User(null, username, password, null, null);
        authRepository.login(activity, credentials, new AuthRepository.LoginApiCallback() {
            @Override
            public void onSuccess(int status, User user, String message) {
                Log.d(TAG, "Login success with status: " + status);
                if (status == 200 && user != null && user.getUserName() != null && user.getToken() != null) {
                    saveUserToPreferences(user, activity);
                    callback.onSuccess(user, "Login successful!");
                } else {
                    callback.onError(message != null ? message : "User data is incomplete.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Login failed: " + error);
                callback.onError("Network error.");
            }
        });
    }

    private void saveUserToPreferences(User user, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE);
        prefs.edit()
                .putString("token", user.getToken() != null ? user.getToken() : "")
                .putString("username", user.getUserName() != null ? user.getUserName() : "")
                .putString("userId", String.valueOf(user.getUserId()))
                .putString("userEmail", user.getEmail() != null ? user.getEmail() : "")
                .putString("userFullname", user.getFullName() != null ? user.getFullName() : "")
                .apply();
    }
}