package com.example.moviesapp.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.moviesapp.Activity.MainActivity;
import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.Domain.User;
import com.example.moviesapp.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView, forgotPasswordTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        usernameEditText = view.findViewById(R.id.userEdt_login);
        passwordEditText = view.findViewById(R.id.passEdt_login);
        loginButton = view.findViewById(R.id.loginBtn);
        registerTextView = view.findViewById(R.id.textViewRegister);
        forgotPasswordTextView = view.findViewById(R.id.textViewForgetPassword);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        registerTextView.setOnClickListener(v -> navigateTo(new SignupFragment()));
        forgotPasswordTextView.setOnClickListener(v -> navigateTo(OtpForgetPassword.newInstance(null)));
    }

    private void attemptLogin() {
        if (getActivity() == null) return;

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast("Please fill in all information.");
            return;
        }

        User credentials = new User(null, username, password, null, null);
        Call<ApiResponse> call = RetrofitClient.getApiService(getActivity()).login(credentials);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (getActivity() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    handleLoginSuccess(response.body());
                } else {
                    showToast("Login failed.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null) {
                    showToast("Network error.");
                }
            }
        });
    }

    private void handleLoginSuccess(ApiResponse apiResponse) {
        if (apiResponse == null) return;

        if (apiResponse.getStatus() == 200) {
            User user = extractUserFromResponse(apiResponse.getData());
            if (user != null && user.getUserName() != null && user.getToken() != null) {
                saveUserToPreferences(user);
                openMainActivity(user);
            } else {
                showToast("User data is incomplete.");
            }
        } else {
            String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Login failed";
            showToast(errorMessage);
        }
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

    private void saveUserToPreferences(User user) {
        if (getActivity() == null) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        prefs.edit()
                .putString("token", user.getToken() != null ? user.getToken() : "")
                .putString("username", user.getUserName() != null ? user.getUserName() : "")
                .putString("userId", String.valueOf(user.getUserId()))
                .putString("userEmail", user.getEmail() != null ? user.getEmail() : "")
                .apply();
    }

    private void openMainActivity(User user) {
        if (getActivity() == null) return;

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("USER_NAME", user.getFullName() != null ? user.getFullName() : "");
        intent.putExtra("USER_ID", user.getUserName() != null ? user.getUserName() : "");
        intent.putExtra("TOKEN", user.getToken() != null ? user.getToken() : "");
        startActivity(intent);
        getActivity().finish();

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
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
