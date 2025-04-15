package com.example.moviesapp.presentation.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.R;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_PERMISSION = 102;

    Uri imageUri;


    public ProfileFragment() {}
    ImageButton btnBack, btnCamera;

    ImageView imgProfile;

    View AccountInfo, ChangePassword,
            PaymentPassword, TransactionHistory;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.navigateTo(ProfileFragment.this, new HomeFragment(), "home_fragment");
            }
        });

        AccountInfo = view.findViewById(R.id.menuAccountInfo);
        ChangePassword = view.findViewById(R.id.menuChangePassword);
        PaymentPassword = view.findViewById(R.id.menuPaymentPassword);
        TransactionHistory = view.findViewById(R.id.menuTransactionHistory);

        AccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.navigateTo(ProfileFragment.this,
                        new AccountInformationFragment(), "account_info_fragment");
            }
        });

        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.navigateTo(ProfileFragment.this,
                        new ChangePassFragment(), "change_pass_fragment");
            }
        });

        PaymentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.navigateTo(ProfileFragment.this,
                        new MyTicketFragment(), "payment_pass_fragment");
            }
        });

        TransactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.navigateTo(ProfileFragment.this,
                        new TransactionHistoryFragment(), "transaction_history_fragment");
                }
        });

        imgProfile = view.findViewById(R.id.imgProfile);
        btnCamera = view.findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(v -> showImagePickerDialog());



        return view;
    }

    private void showImagePickerDialog() {
        String[] options = {"Chụp ảnh", "Chọn từ thư viện"};
        new AlertDialog.Builder(getContext())
                .setTitle("Tải ảnh lên")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSION);
                        } else {
                            openCamera(); // nếu đã cấp thì mở camera luôn
                        }
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
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
                e.printStackTrace();
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
            if (requestCode == REQUEST_CAMERA && data != null) {
                imgProfile.setImageURI(imageUri);
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                imageUri = data.getData();
                imgProfile.setImageURI(imageUri);
            }
        }
    }
}
