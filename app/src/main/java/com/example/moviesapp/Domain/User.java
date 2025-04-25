package com.example.moviesapp.Domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("userId")
    @Expose
    private int userId;

    @SerializedName("fullName")
    @Expose
    private String fullName;

    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("otp")
    @Expose
    private String otp;

    public User(String fullName, String userName, String password, String email, String phoneNumber) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.otp = null;
        this.token = null;
        this.userId = 0;
    }
    public User() {
        this.userId = 0;
        this.fullName = null;
        this.userName = null;
        this.password = null;
        this.email = null;
        this.phoneNumber = null;
        this.token = null;
        this.otp = null;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    // Để dễ debug, thêm toString()
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", token='" + token + '\'' +
                ", otp='" + otp + '\'' +
                '}';
    }
}