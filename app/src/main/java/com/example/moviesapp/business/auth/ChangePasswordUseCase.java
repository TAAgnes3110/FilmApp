package com.example.moviesapp.business.auth;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.example.moviesapp.data.model.User;
import com.example.moviesapp.data.repository.AuthRepository;

public class ChangePasswordUseCase {
    private AuthRepository authRepository = new AuthRepository();
    private String userEmail;
    private static final String TAG = "ChangePasswordUseCase";

    public interface ChangePasswordCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void changePassword(String password, String confirmPassword, Activity activity, ChangePasswordCallback callback) {
        if (!validateInput(password, confirmPassword)) {
            callback.onError("Invalid input. Please check your passwords.");
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            callback.onError("Missing email information!");
            return;
        }

        User payload = new User(null, null, password, userEmail, null);
        Log.d(TAG, "Sending change password request for: " + userEmail);
        authRepository.changePassword(activity, payload, new AuthRepository.ApiCallback() {
            @Override
            public void onSuccess(int status, String message) {
                Log.d(TAG, "Change password success with status: " + status);
                if (status == 200) {
                    callback.onSuccess("Password changed successfully!");
                } else {
                    callback.onError(message != null ? message : "Password change failed.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Change password failed: " + error);
                callback.onError("Network error while changing password: " + error);
            }
        });
    }

    private boolean validateInput(String password, String confirmPassword) {
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }
}