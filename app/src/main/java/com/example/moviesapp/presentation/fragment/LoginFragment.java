package com.example.moviesapp.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.moviesapp.R;
import com.example.moviesapp.Activity.MainActivity;
import com.example.moviesapp.business.auth.LoginUseCase;
import com.example.moviesapp.data.model.User;

public class LoginFragment extends Fragment {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView, forgotPasswordTextView;
    private LoginUseCase useCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        useCase = new LoginUseCase();
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
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            useCase.attemptLogin(username, password, getActivity(), new LoginUseCase.LoginCallback() {
                @Override
                public void onSuccess(User user, String message) {
                    showToast(message);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("USER_FULLNAME", user.getFullName() != null ? user.getFullName() : "");
                    intent.putExtra("USER_ID", user.getUserName() != null ? user.getUserName() : "");
                    intent.putExtra("TOKEN", user.getToken() != null ? user.getToken() : "");
                    startActivity(intent);
                    getActivity().finish();
                }

                @Override
                public void onError(String message) {
                    showToast(message);
                }
            });
        });

        registerTextView.setOnClickListener(v -> navigateTo(new SignupFragment()));
        forgotPasswordTextView.setOnClickListener(v -> navigateTo(OtpForgetPasswordFragment.newInstance(null)));
    }

    private void navigateTo(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        usernameEditText = null;
        passwordEditText = null;
        loginButton = null;
        registerTextView = null;
        forgotPasswordTextView = null;
        if (loginButton != null) loginButton.setOnClickListener(null);
        if (registerTextView != null) registerTextView.setOnClickListener(null);
        if (forgotPasswordTextView != null) forgotPasswordTextView.setOnClickListener(null);
        useCase = null;
    }
}