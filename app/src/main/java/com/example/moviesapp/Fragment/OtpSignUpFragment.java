package com.example.moviesapp.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.Domain.OtpRequest;
import com.example.moviesapp.Domain.User;
import com.example.moviesapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpSignUpFragment extends Fragment {

    private EditText otpEditText;
    private TextView timerTextView, resendTextView, backToSignUpTextView;
    private Button verifyButton;
    private CountDownTimer countDownTimer;
    private User user;

    private static final long TIMER_DURATION_MS = 180_000;
    private static final long TIMER_INTERVAL_MS = 1_000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            if (user == null) {
                navigateTo(new SignupFragment());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        initViews(view);
        setupListeners();
        if (user != null) startTimer();
        return view;
    }

    private void initViews(View view) {
        otpEditText = view.findViewById(R.id.otpEdt);
        timerTextView = view.findViewById(R.id.textViewTimer);
        resendTextView = view.findViewById(R.id.textViewResend);
        backToSignUpTextView = view.findViewById(R.id.textViewBackToSignup);
        verifyButton = view.findViewById(R.id.verifyOtpBtn);
    }

    private void setupListeners() {
        verifyButton.setOnClickListener(v -> verifyOtpAndSignUp());
        resendTextView.setOnClickListener(v -> resendOtp());
        backToSignUpTextView.setOnClickListener(v -> navigateTo(new SignupFragment()));
    }

    private void startTimer() {
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

    private void verifyOtpAndSignUp() {
        if (user == null) return;

        String otp = otpEditText.getText().toString().trim();
        if (TextUtils.isEmpty(otp)) {
            showToast("Please enter the OTP.");
            return;
        }

        OtpRequest request = new OtpRequest(user.getEmail(), otp);
        Call<ApiResponse> verifyCall = RetrofitClient.getApiService(getActivity()).verifyOtp(request);
        verifyCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        completeSignup();
                    } else {
                        showToast(apiResponse.getMessage() != null ? apiResponse.getMessage() : "OTP verification failed.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error during OTP verification.");
            }
        });
    }

    private void completeSignup() {
        Call<ApiResponse> signupCall = RetrofitClient.getApiService(getActivity()).signup(user);
        signupCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 201) {
                        showToast("Signup successful! Please log in.");
                        navigateTo(new LoginFragment());
                    } else {
                        showToast(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Signup failed.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error during signup.");
            }
        });
    }

    private void resendOtp() {
        if (user == null) return;

        OtpRequest request = new OtpRequest(user.getEmail(), "signup", true);
        Call<ApiResponse> call = RetrofitClient.getApiService(getActivity()).sendOtpForSignup(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        startTimer();
                    } else {
                        showToast(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to resend OTP.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error while resending OTP.");
            }
        });
    }

    private void navigateTo(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showToast(String message) {
        if (getActivity() != null && message != null && !message.isEmpty()) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
