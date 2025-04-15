package com.example.moviesapp.business.auth;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.example.moviesapp.data.repository.AuthRepository;
import com.example.moviesapp.data.model.OtpRequest;

public class OtpForgetPasswordUseCase {
    private AuthRepository authRepository = new AuthRepository();
    private String userEmail;
    private CountDownTimer countDownTimer;
    private long otpSentTime;
    private boolean isOtpSent = false;
    private static final long TIMER_DURATION_MS = 180_000;
    private static final long TIMER_INTERVAL_MS = 1_000;
    private static final long OTP_EXPIRY_MS = 180_000;
    private static final String TAG = "OtpForgetPasswordUseCase";

    public interface OtpCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public String getUserEmail() {
        return userEmail != null ? userEmail : "";
    }

    public void startTimer(TextView textViewResendTime, AppCompatButton buttonSendOtp) {
        buttonSendOtp.setEnabled(false);
        textViewResendTime.setVisibility(View.VISIBLE);
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(TIMER_DURATION_MS, TIMER_INTERVAL_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 60000);
                int seconds = (int) ((millisUntilFinished % 60000) / 1000);
                textViewResendTime.setText(String.format("Please wait %02d:%02d to resend OTP", minutes, seconds));
            }

            @Override
            public void onFinish() {
                textViewResendTime.setVisibility(View.GONE);
                buttonSendOtp.setEnabled(true);
                isOtpSent = false;
            }
        }.start();
    }

    public void sendOtp(String email, Activity activity, OtpCallback callback) {
        if (!validateEmail(email)) {
            callback.onError("Invalid email format.");
            return;
        }
        if (isOtpSent && (System.currentTimeMillis() - otpSentTime < TIMER_DURATION_MS)) {
            callback.onError("Please wait before requesting another OTP!");
            return;
        }

        userEmail = email;
        OtpRequest request = new OtpRequest(email);
        authRepository.sendOtp(activity, request, new AuthRepository.ApiCallback() {
            @Override
            public void onSuccess(int status, String message) {
                Log.d(TAG, "Send OTP success with status: " + status);
                if (status == 200) {
                    isOtpSent = true;
                    otpSentTime = System.currentTimeMillis();
                    callback.onSuccess("OTP has been sent to your email.");
                } else {
                    callback.onError(message != null ? message : "Failed to send OTP.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Send OTP failed: " + error);
                callback.onError("Network error while sending OTP: " + error);
            }
        });
    }

    public void verifyOtp(String email, String otp, Activity activity, OtpCallback callback) {
        if (!validateInput(email, otp)) {
            callback.onError("Please enter valid email and OTP.");
            return;
        }
        if (System.currentTimeMillis() - otpSentTime > OTP_EXPIRY_MS) {
            callback.onError("OTP has expired. Please request a new one.");
            return;
        }

        userEmail = email;
        OtpRequest request = new OtpRequest(email, otp);
        authRepository.verifyOtp(activity, request, new AuthRepository.ApiCallback() {
            @Override
            public void onSuccess(int status, String message) {
                Log.d(TAG, "Verify OTP success with status: " + status);
                if (status == 200) {
                    callback.onSuccess("OTP verified successfully!");
                } else {
                    callback.onError(message != null ? message : "OTP verification failed.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Verify OTP failed: " + error);
                callback.onError("Network error while verifying OTP: " + error);
            }
        });
    }

    private boolean validateInput(String email, String otp) {
        return validateEmail(email) && !TextUtils.isEmpty(otp);
    }

    private boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void cancelTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
    }
}