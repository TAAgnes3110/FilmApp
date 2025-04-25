package com.example.moviesapp.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.Domain.OtpRequest;
import com.example.moviesapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpForgetPassword extends Fragment {
    private EditText editTextEmail, editTextOTP;
    private TextView textViewResendTime, textViewBackLog;
    private AppCompatButton buttonSendOtp, buttonVerify;
    private CountDownTimer countDownTimer;
    private String userEmail;
    private long otpSentTime;
    private boolean isOtpSent = false;
    private static final long TIMER_DURATION_MS = 180_000;
    private static final long TIMER_INTERVAL_MS = 1_000;
    private static final long OTP_EXPIRY_MS = 180_000;
    private static final String TAG = "OtpForgetPassword";

    public static OtpForgetPassword newInstance(String email) {
        OtpForgetPassword fragment = new OtpForgetPassword();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userEmail = getArguments() != null ? getArguments().getString("email") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_forget_password, container, false);
        initUI(view);
        setupListeners();
        if (!TextUtils.isEmpty(userEmail)) {
            editTextEmail.setText(userEmail);
        }
        return view;
    }

    private void initUI(View view) {
        editTextEmail = view.findViewById(R.id.emailEditText);
        editTextOTP = view.findViewById(R.id.otpEditText);
        textViewResendTime = view.findViewById(R.id.textViewResendTime);
        textViewBackLog = view.findViewById(R.id.textViewBackToLogin);
        buttonSendOtp = view.findViewById(R.id.sendOtpButton);
        buttonVerify = view.findViewById(R.id.changePasswordButton);
    }

    private void setupListeners() {
        buttonSendOtp.setOnClickListener(v -> sendOtp());
        buttonVerify.setOnClickListener(v -> verifyOtp());
        textViewBackLog.setOnClickListener(v -> navigateTo(new LoginFragment()));
    }

    private void startTimer() {
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

    private void sendOtp() {
        String email = editTextEmail.getText().toString().trim();
        if (!validateEmail(email)) return;
        if (isOtpSent && (System.currentTimeMillis() - otpSentTime < TIMER_DURATION_MS)) {
            showToast("Please wait before requesting another OTP!");
            return;
        }

        userEmail = email;
        OtpRequest request = new OtpRequest(email);
        Call<ApiResponse> call = RetrofitClient.getApiService(getActivity()).sendOtp(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Send OTP response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        showToast("OTP has been sent to your email.");
                        isOtpSent = true;
                        otpSentTime = System.currentTimeMillis();
                        startTimer();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to send OTP.";
                        showToast(message);
                    }
                } else {
                    showToast("Failed to send OTP. (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error while sending OTP: " + t.getMessage());
            }
        });
    }

    private void verifyOtp() {
        String email = editTextEmail.getText().toString().trim();
        String otp = editTextOTP.getText().toString().trim();

        if (!validateInput(email, otp)) return;
        if (System.currentTimeMillis() - otpSentTime > OTP_EXPIRY_MS) {
            showToast("OTP has expired. Please request a new one.");
            return;
        }

        userEmail = email;
        OtpRequest request = new OtpRequest(email, otp);
        Call<ApiResponse> call = RetrofitClient.getApiService(getActivity()).verifyOtp(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Verify OTP response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        showToast("OTP verified successfully!");
                        navigateTo(ChangePasswordFragment.newInstance(userEmail, null));
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "OTP verification failed.";
                        showToast(message);
                    }
                } else {
                    showToast("OTP verification failed. (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error while verifying OTP: " + t.getMessage());
            }
        });
    }

    private boolean validateInput(String email, String otp) {
        if (!validateEmail(email)) return false;
        if (TextUtils.isEmpty(otp)) {
            showToast("Please enter the OTP.");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            showToast("Please enter your email.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format.");
            return false;
        }
        return true;
    }

    private void navigateTo(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            showToast("Unable to navigate to the next screen.");
        }
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
