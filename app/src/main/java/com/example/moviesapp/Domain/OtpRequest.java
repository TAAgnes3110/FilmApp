package com.example.moviesapp.Domain;

import java.io.Serializable;

public class OtpRequest implements Serializable {
    private String email;
    private String otp;
    private String context;

    public OtpRequest(String email) {
        this.email = email;
    }

    public OtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
    public OtpRequest(String email, String context, boolean isForSending) {
        this.email = email;
        this.context = context;
    }
    public OtpRequest(String email, String otp, String context) {
        this.email = email;
        this.otp = otp;
        this.context = context;
    }

    // Getters và Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}