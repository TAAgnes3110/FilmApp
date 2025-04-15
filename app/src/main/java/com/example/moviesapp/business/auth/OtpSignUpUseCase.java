package com.example.moviesapp.business.auth;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.moviesapp.data.repository.AuthRepository;
import com.example.moviesapp.data.model.OtpRequest;
import com.example.moviesapp.data.model.User;

public class OtpSignUpUseCase {
    private AuthRepository authRepository = new AuthRepository();
    private User user;
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION_MS = 180_000;
    private static final long TIMER_INTERVAL_MS = 1_000;
    private static final String TAG = "OtpSignUpUseCase";

    public interface OtpCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void startTimer(TextView timerTextView, TextView resendTextView) {
        resendTextView.setEnabled(false);
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(TIMER_DURATION_MS, TIMER_INTERVAL_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 60000);
                int seconds = (int) ((millisUntilFinished % 60000) / 1000);
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                resendTextView.setEnabled(true);
            }
        }.start();
    }

    public void verifyOtpAndSignUp(String otp, Activity activity, OtpCallback callback) {
        if (user == null || TextUtils.isEmpty(otp)) {
            callback.onError("Please enter the OTP.");
            return;
        }

        OtpRequest request = new OtpRequest(user.getEmail(), otp);
        authRepository.verifyOtp(activity, request, new AuthRepository.ApiCallback() {
            @Override
            public void onSuccess(int status, String message) {
                Log.d(TAG, "Verify OTP success with status: " + status);
                if (status == 200) {
                    completeSignup(activity, callback);
                } else {
                    callback.onError(message != null ? message : "OTP verification failed.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Verify OTP failed: " + error);
                callback.onError("Network error during OTP verification.");
            }
        });
    }

    private void completeSignup(Activity activity, OtpCallback callback) {
        authRepository.signup(activity, user, new AuthRepository.ApiCallback() {
            @Override
            public void onSuccess(int status, String message) {
                Log.d(TAG, "Signup success with status: " + status);
                if (status == 201) {
                    callback.onSuccess("Signup successful! Please log in.");
                } else {
                    callback.onError(message != null ? message : "Signup failed.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Signup failed: " + error);
                callback.onError("Network error during signup.");
            }
        });
    }

    public void resendOtp(Activity activity, OtpCallback callback) {
        if (user == null) {
            callback.onError("User data is missing.");
            return;
        }

        OtpRequest request = new OtpRequest(user.getEmail(), "signup", true);
        authRepository.sendOtpForSignup(activity, request, new AuthRepository.ApiCallback() {
            @Override
            public void onSuccess(int status, String message) {
                Log.d(TAG, "Resend OTP success with status: " + status);
                if (status == 200) {
                    callback.onSuccess("OTP resent successfully!");
                } else {
                    callback.onError(message != null ? message : "Failed to resend OTP.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Resend OTP failed: " + error);
                callback.onError("Network error while resending OTP.");
            }
        });
    }

    public void cancelTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
    }
}