package com.example.moviesapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.Api.ApiService;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Domain.User;
import com.example.moviesapp.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountInformationFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvFullname, tvUsername, tvPassword, tvEmail, tvPhone;
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inforaccount, container, false);

        // Khởi tạo các view
        btnBack = view.findViewById(R.id.btnBackI4Acc);
        tvFullname = view.findViewById(R.id.tv_fullname_value);
        tvUsername = view.findViewById(R.id.tv_username_value);
        tvPassword = view.findViewById(R.id.tv_password_value);
        tvEmail = view.findViewById(R.id.tv_email_value);
        tvPhone = view.findViewById(R.id.tv_phone_value);

        // Khởi tạo ApiService từ RetrofitClient
        apiService = RetrofitClient.getApiService(requireContext());

        // Xử lý sự kiện nút Back
        btnBack.setOnClickListener(v -> {
            // Quay lại fragment trước đó
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Gọi API để lấy thông tin người dùng
        fetchUserInfo();

        return view;
    }

    private void fetchUserInfo() {
        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            navigateTo(new LoginFragment());
            return;
        }

        // Gọi API getUserById
        Call<ApiResponse> call = apiService.getUserById(userId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        try {
                            // Chuyển đổi data thành User
                            Gson gson = new Gson();
                            String jsonData = gson.toJson(apiResponse.getData());
                            User user = gson.fromJson(jsonData, User.class);

                            if (user != null) {
                                tvFullname.setText(user.getFullName() != null ? user.getFullName() : "");
                                tvUsername.setText(user.getUserName() != null ? user.getUserName() : "");
                                tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                                tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
                                // Hiển thị mật khẩu hash
                                tvPassword.setText(user.getPassword() != null ? user.getPassword() : "");
                            } else {
                                Toast.makeText(getContext(), "Dữ liệu người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
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