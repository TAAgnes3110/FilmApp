package com.example.moviesapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Api.ApiService;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.Domain.ChangePasswordRequest;
import com.example.moviesapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassFragment extends Fragment {

    private ImageButton buttonBack;
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changepass, container, false);

        // Khởi tạo các view
        buttonBack = view.findViewById(R.id.buttonBack);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        apiService = RetrofitClient.getApiService(requireContext());

        buttonBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnChangePassword.setOnClickListener(v -> attemptChangePassword());

        return view;
    }

    private void attemptChangePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra đầu vào
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy userId, email, token từ SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        String email = prefs.getString("email", null);
        String token = prefs.getString("token", null);

        if (userId == null || email == null || token == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            navigateTo(new LoginFragment());
            return;
        }

        ChangePasswordRequest payload = new ChangePasswordRequest();
        payload.setUserId(Integer.parseInt(userId));
        payload.setEmail(email);
        payload.setOldPassword(oldPassword);
        payload.setNewPassword(newPassword);

        // Gọi API đổi mật khẩu
        Call<ApiResponse> call = apiService.changePasswordSecure("Bearer " + token, payload);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                        prefs.edit().clear().apply();
                        navigateTo(new LoginFragment());
                    } else {
                        Toast.makeText(getContext(), "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}