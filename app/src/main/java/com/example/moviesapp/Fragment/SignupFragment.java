package com.example.moviesapp.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

public class SignupFragment extends Fragment {
    private static final String TAG = "SignupFragment";
    private EditText usernameEditText, passwordEditText, emailEditText, fullNameEditText, phoneEditText;
    private Button signupButton;
    private TextView loginTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        usernameEditText = view.findViewById(R.id.userEdt_signup);
        passwordEditText = view.findViewById(R.id.passEdt_signup);
        emailEditText = view.findViewById(R.id.emailEdt_signup);
        fullNameEditText = view.findViewById(R.id.hoTenEdt_signup);
        phoneEditText = view.findViewById(R.id.soDienThoaiEdt_signup);
        signupButton = view.findViewById(R.id.signupBtn);
        loginTextView = view.findViewById(R.id.textViewLogin);
    }

    private void setupListeners() {
        signupButton.setOnClickListener(v -> handleSignup());
        loginTextView.setOnClickListener(v -> navigateTo(new LoginFragment()));
    }

    private void handleSignup() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        if (!validateInput(email, password, fullName, phone, username)) return;

        User user = new User(fullName, username, password, email, phone);
        OtpRequest otpRequest = new OtpRequest(email, "signup", true);
        Call<ApiResponse> sendOtpCall = RetrofitClient.getApiService(getActivity()).sendOtpForSignup(otpRequest);
        sendOtpCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Send OTP response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        showToast("OTP has been sent to your email!");
                        Bundle args = new Bundle();
                        args.putSerializable("user", user);
                        OtpSignUpFragment otpFragment = new OtpSignUpFragment();
                        otpFragment.setArguments(args);
                        navigateTo(otpFragment);
                    } else {
                        showToast(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to send OTP.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private boolean validateInput(String email, String password, String fullName, String phone, String username) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(username)) {
            showToast("Please fill in all fields.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format.");
            return false;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters.");
            return false;
        }

        if (!phone.matches("\\d{10,11}")) {
            showToast("Invalid phone number. It must contain 10-11 digits.");
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
            showToast("Failed to navigate.");
        }
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
