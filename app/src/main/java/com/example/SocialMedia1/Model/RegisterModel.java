package com.example.SocialMedia1.Model;

import com.google.gson.annotations.SerializedName;

public class RegisterModel {
    @SerializedName("name")
    String name;
    @SerializedName("member")
    String member;
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;

    public RegisterModel(String name, String member, String email, String password) {
        this.name = name;
        this.member = member;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
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
