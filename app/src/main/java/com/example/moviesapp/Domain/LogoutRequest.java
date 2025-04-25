package com.example.moviesapp.Domain;

public class LogoutRequest {
    private final String userId;
    private final String token;

    public LogoutRequest(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}