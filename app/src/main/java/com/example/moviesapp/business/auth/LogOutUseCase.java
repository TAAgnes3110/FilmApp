package com.example.moviesapp.business.auth;

import com.example.moviesapp.data.repository.AuthRepository;

public class LogOutUseCase {
    private final AuthRepository authRepository;

    public LogOutUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(android.app.Activity activity, String userId, String token, AuthRepository.ApiCallback callback) {
        authRepository.logout(activity, userId, token, callback);
    }
}