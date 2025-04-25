package com.example.moviesapp.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.Domain.User;
import com.example.moviesapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {
    private String userEmail;
    private EditText editTextNewPassword, editTextConfirmPassword;
    private Button btnChangePassword;
    private static final String TAG = "ChangePasswordFragment";

    public static ChangePasswordFragment newInstance(String email, String otp) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString("email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        initUI(view);
        setupListeners();
        return view;
    }

    private void initUI(View view) {
        editTextNewPassword = view.findViewById(R.id.newPasswordEditText);
        editTextConfirmPassword = view.findViewById(R.id.confirmPasswordEditText);
        btnChangePassword = view.findViewById(R.id.changePasswordButton);
    }

    private void setupListeners() {
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String password = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (!validateInput(password, confirmPassword)) return;
        if (TextUtils.isEmpty(userEmail)) {
            showToast("Missing email information!");
            return;
        }

        User payload = new User(null, null, password, userEmail, null);
        Log.d(TAG, "Sending change password request for: " + userEmail);
        Call<ApiResponse> call = RetrofitClient.getApiService(getActivity()).changePassword(null, payload);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "Change password response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Status: " + apiResponse.getStatus() + ", Message: " + apiResponse.getMessage());
                    if (apiResponse.getStatus() == 200) {
                        showToast("Password changed successfully!");
                        navigateTo(new LoginFragment());
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Password change failed.";
                        showToast(message);
                    }
                } else {
                    showToast("Password change failed: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error while changing password: " + t.getMessage());
            }
        });
    }

    private boolean validateInput(String password, String confirmPassword) {
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            showToast("Please fill in all fields!");
            return false;
        }
        if (password.length() < 6) {
            showToast("Password must be at least 6 characters long!");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match!");
            return false;
        }
        return true;
    }

    private void navigateTo(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            showToast("Failed to navigate!");
        }
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
