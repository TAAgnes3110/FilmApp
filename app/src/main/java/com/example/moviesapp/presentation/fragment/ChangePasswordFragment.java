package com.example.moviesapp.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.moviesapp.R;
import com.example.moviesapp.business.auth.ChangePasswordUseCase;

public class ChangePasswordFragment extends Fragment {
    private EditText editTextNewPassword, editTextConfirmPassword;
    private Button btnChangePassword;
    private ChangePasswordUseCase useCase;

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
        useCase = new ChangePasswordUseCase();
        if (getArguments() != null) {
            useCase.setUserEmail(getArguments().getString("email"));
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
        btnChangePassword.setOnClickListener(v -> {
            String password = editTextNewPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();
            useCase.changePassword(password, confirmPassword, getActivity(), new ChangePasswordUseCase.ChangePasswordCallback() {
                @Override
                public void onSuccess(String message) {
                    showToast(message);
                    navigateTo(new LoginFragment());
                }

                @Override
                public void onError(String message) {
                    showToast(message);
                }
            });
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        editTextNewPassword = null;
        editTextConfirmPassword = null;
        btnChangePassword = null;
        if (btnChangePassword != null) btnChangePassword.setOnClickListener(null);
        useCase = null;
    }
}