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
import com.example.moviesapp.business.auth.OtpSignUpUseCase;
import com.example.moviesapp.data.model.User;

public class OtpSignUpFragment extends Fragment {
    private EditText otpEditText;
    private TextView timerTextView, resendTextView, backToSignUpTextView;
    private Button verifyButton;
    private OtpSignUpUseCase useCase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useCase = new OtpSignUpUseCase();
        if (getArguments() != null) {
            useCase.setUser((User) getArguments().getSerializable("user"));
            if (useCase.getUser() == null) {
                navigateTo(new SignupFragment());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        initViews(view);
        setupListeners();
        if (useCase.getUser() != null) useCase.startTimer(timerTextView, resendTextView);
        return view;
    }

    private void initViews(View view) {
        otpEditText = view.findViewById(R.id.otpEdt);
        timerTextView = view.findViewById(R.id.textViewTimer);
        resendTextView = view.findViewById(R.id.textViewResend);
        backToSignUpTextView = view.findViewById(R.id.textViewBackToSignup);
        verifyButton = view.findViewById(R.id.verifyOtpBtn);
    }

    private void setupListeners() {
        verifyButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            useCase.verifyOtpAndSignUp(otp, getActivity(), new OtpSignUpUseCase.OtpCallback() {
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

        resendTextView.setOnClickListener(v -> useCase.resendOtp(getActivity(), new OtpSignUpUseCase.OtpCallback() {
            @Override
            public void onSuccess(String message) {
                showToast(message);
                useCase.startTimer(timerTextView, resendTextView);
            }

            @Override
            public void onError(String message) {
                showToast(message);
            }
        }));

        backToSignUpTextView.setOnClickListener(v -> navigateTo(new SignupFragment()));
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
        if (getActivity() != null && message != null && !message.isEmpty()) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        useCase.cancelTimer();
        otpEditText = null;
        timerTextView = null;
        resendTextView = null;
        backToSignUpTextView = null;
        verifyButton = null;
        if (verifyButton != null) verifyButton.setOnClickListener(null);
        if (resendTextView != null) resendTextView.setOnClickListener(null);
        if (backToSignUpTextView != null) backToSignUpTextView.setOnClickListener(null);
        useCase = null;
    }
}