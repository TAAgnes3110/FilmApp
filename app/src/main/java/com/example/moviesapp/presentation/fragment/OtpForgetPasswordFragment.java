package com.example.moviesapp.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.R;
import com.example.moviesapp.business.auth.OtpForgetPasswordUseCase;

public class OtpForgetPasswordFragment extends Fragment {
    private EditText editTextEmail, editTextOTP;
    private TextView textViewResendTime, textViewBackLog;
    private AppCompatButton buttonSendOtp, buttonVerify;
    private OtpForgetPasswordUseCase useCase;

    public static OtpForgetPasswordFragment newInstance(String email) {
        OtpForgetPasswordFragment fragment = new OtpForgetPasswordFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useCase = new OtpForgetPasswordUseCase();
        String userEmail = getArguments() != null ? getArguments().getString("email") : "";
        useCase.setUserEmail(userEmail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_forget_password, container, false);
        initUI(view);
        setupListeners();
        if (!useCase.getUserEmail().isEmpty()) {
            editTextEmail.setText(useCase.getUserEmail());
        }
        return view;
    }

    private void initUI(View view) {
        editTextEmail = view.findViewById(R.id.emailEditText);
        editTextOTP = view.findViewById(R.id.otpEditText);
        textViewResendTime = view.findViewById(R.id.textViewResendTime);
        textViewBackLog = view.findViewById(R.id.textViewBackToLogin);
        buttonSendOtp = view.findViewById(R.id.sendOtpButton);
        buttonVerify = view.findViewById(R.id.changePasswordButton);
    }

    private void setupListeners() {
        buttonSendOtp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            useCase.sendOtp(email, getActivity(), new OtpForgetPasswordUseCase.OtpCallback() {
                @Override
                public void onSuccess(String message) {
                    showToast(message);
                    textViewResendTime.setVisibility(View.VISIBLE);
                    buttonSendOtp.setEnabled(false);
                    useCase.startTimer(textViewResendTime, buttonSendOtp);
                }

                @Override
                public void onError(String message) {
                    showToast(message);
                }
            });
        });

        buttonVerify.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String otp = editTextOTP.getText().toString().trim();
            useCase.verifyOtp(email, otp, getActivity(), new OtpForgetPasswordUseCase.OtpCallback() {
                @Override
                public void onSuccess(String message) {
                    showToast(message);
                    navigateTo(ChangePasswordFragment.newInstance(email, null));
                }

                @Override
                public void onError(String message) {
                    showToast(message);
                }
            });
        });

        textViewBackLog.setOnClickListener(v -> navigateTo(new LoginFragment()));
    }

    private void navigateTo(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            showToast("Unable to navigate.");
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
        useCase.cancelTimer();
    }
}