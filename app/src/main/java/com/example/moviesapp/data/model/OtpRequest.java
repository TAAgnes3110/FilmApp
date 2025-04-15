package com.example.moviesapp.data.model;

public class OtpRequest {
    private String email;
    private String otp;
    private String type;
    private boolean sendOtp;

    public OtpRequest(String email) {
        this.email = email;
    }

    public OtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public OtpRequest(String email, String type, boolean sendOtp) {
        this.email = email;
        this.type = type;
        this.sendOtp = sendOtp;
    }

    // Getters
    public String getEmail() { return email; }
    public String getOtp() { return otp; }
    public String getType() { return type; }
    public boolean isSendOtp() { return sendOtp; }
}