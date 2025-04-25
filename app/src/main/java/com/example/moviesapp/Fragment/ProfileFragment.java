package com.example.moviesapp.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.Activity.AuthActivity;
import com.example.moviesapp.Api.ApiResponse;
import com.example.moviesapp.Api.ApiService;
import com.example.moviesapp.Api.RetrofitClient;
import com.example.moviesapp.R;
import com.example.moviesapp.Domain.User;
import com.example.moviesapp.Domain.LogoutRequest;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView tvName;
    private ImageButton btnBack, btnCamera;
    private ImageView imgProfile;
    private View accountInfo, changePassword, paymentPassword, logOut;

    private ApiService apiService;

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_PERMISSION = 102;

    private Uri imageUri;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);
        setupListeners();
        fetchUserInfo();
        return view;
    }

    private void initUI(View view) {
        accountInfo = view.findViewById(R.id.menuAccountInfo);
        changePassword = view.findViewById(R.id.menuChangePassword);
        paymentPassword = view.findViewById(R.id.menuPaymentPassword);
        tvName = view.findViewById(R.id.tvName);
        imgProfile = view.findViewById(R.id.imgProfile);
        btnCamera = view.findViewById(R.id.btnCamera);
        btnBack = view.findViewById(R.id.btnBack);
        logOut = view.findViewById(R.id.menuLogOut);
        apiService = RetrofitClient.getApiService(requireContext());
    }

    private void setupListeners() {
        btnCamera.setOnClickListener(v -> showImagePickerDialog());
        imgProfile.setOnClickListener(v -> showImagePickerDialog());
        accountInfo.setOnClickListener(v -> NavigationHelper.navigateTo(ProfileFragment.this,
                new AccountInformationFragment(), "account_info_fragment"));
        changePassword.setOnClickListener(v -> NavigationHelper.navigateTo(ProfileFragment.this,
                new ChangePassFragment(), "change_pass_fragment"));
        paymentPassword.setOnClickListener(v -> NavigationHelper.navigateTo(ProfileFragment.this,
                new MyTicketFragment(), "payment_pass_fragment"));
        btnBack.setOnClickListener(v -> NavigationHelper.navigateTo(ProfileFragment.this,
                new HomeFragment(), "home_fragment"));
        logOut.setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        String token = prefs.getString("token", null);

        if (userId == null || token == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            prefs.edit().clear().apply();
            navigateToAuthActivity();
            return;
        }

        String authHeader = "Bearer " + token;
        Call<ApiResponse> call = apiService.logout(authHeader, new LogoutRequest(userId, token));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("ProfileFragment", "Mã phản hồi đăng xuất: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    prefs.edit().clear().apply();
                    navigateToAuthActivity();
                } else {
                    Toast.makeText(getContext(), "Đăng xuất thất bại. (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Đăng xuất thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToAuthActivity() {
        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack hoạt động
        startActivity(intent);
        requireActivity().finish(); // Kết thúc MainActivity
    }

    private void fetchUserInfo() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("AccountInfo", "UserId: " + userId);

        if (userId == null) {
            Toast.makeText(getContext(), "Please log in again", Toast.LENGTH_SHORT).show();
            navigateTo(new LoginFragment());
            return;
        }

        Call<ApiResponse> call = apiService.getUserById(userId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        try {
                            Gson gson = new Gson();
                            String jsonData = gson.toJson(apiResponse.getData());
                            User user = gson.fromJson(jsonData, User.class);

                            if (user != null) {
                                tvName.setText(user.getFullName() != null ? user.getFullName() : "");
                            } else {
                                Toast.makeText(getContext(), "Invalid user data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Data analysis error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), apiResponse.getMessage() != null ? apiResponse.getMessage() : "Unknown error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Your session has expired. Please log in again.", Toast.LENGTH_LONG).show();
                        prefs.edit().clear().apply();
                        navigateTo(new LoginFragment());
                    } else {
                        Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Take photo", "Choose from gallery"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Upload photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSION);
                        } else {
                            openCamera();
                        }
                    } else {
                        openGallery();
                    }
                })
                .show();
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

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                String fileName = "profile_photo_" + System.currentTimeMillis();
                File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                photoFile = File.createTempFile(fileName, ".jpg", storageDir);
                imageUri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && imageUri != null) {
                imgProfile.setImageURI(imageUri);
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                imageUri = data.getData();
                imgProfile.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tvName = null;
        btnBack = null;
        btnCamera = null;
        imgProfile = null;
        accountInfo = null;
        changePassword = null;
        paymentPassword = null;
        logOut = null;
        if (btnCamera != null) btnCamera.setOnClickListener(null);
        if (imgProfile != null) imgProfile.setOnClickListener(null);
        if (accountInfo != null) accountInfo.setOnClickListener(null);
        if (changePassword != null) changePassword.setOnClickListener(null);
        if (paymentPassword != null) paymentPassword.setOnClickListener(null);
        if (btnBack != null) btnBack.setOnClickListener(null);
        if (logOut != null) logOut.setOnClickListener(null);
        imageUri = null;
    }
}