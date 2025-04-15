package com.example.moviesapp.presentation.fragment;

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
import com.example.moviesapp.business.auth.SignupUseCase;
import com.example.moviesapp.data.model.User;

public class SignupFragment extends Fragment {
    private EditText usernameEditText, passwordEditText, emailEditText, fullNameEditText, phoneEditText;
    private Button signupButton;
    private TextView loginTextView;
    private SignupUseCase useCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        useCase = new SignupUseCase();
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
        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String fullName = fullNameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();

            User user = new User(fullName, username, password, email, phone);
            useCase.handleSignup(user, getActivity(), new SignupUseCase.SignupCallback() {
                @Override
                public void onSuccess(String message) {
                    showToast(message);
                    Bundle args = new Bundle();
                    args.putSerializable("user", user);
                    OtpSignUpFragment otpFragment = new OtpSignUpFragment();
                    otpFragment.setArguments(args);
                    navigateTo(otpFragment);
                }

                @Override
                public void onError(String message) {
                    showToast(message);
                }
            });
        });

        loginTextView.setOnClickListener(v -> navigateTo(new LoginFragment()));
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