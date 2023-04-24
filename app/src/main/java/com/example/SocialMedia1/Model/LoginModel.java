package com.example.SocialMedia1.Model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("uid")
    String uid;

    public LoginModel(String email, String password,String uid) {
        this.email = email;
        this.password = password;
        this.uid = uid;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
