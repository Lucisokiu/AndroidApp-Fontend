package com.example.SocialMedia1.Model;

import okhttp3.MultipartBody;

public class ImagePost {
    private MultipartBody.Part image;

    public ImagePost(MultipartBody.Part image) {
        this.image = image;
    }

    public MultipartBody.Part getImage() {
        return image;
    }

    public void setImage(MultipartBody.Part image) {
        this.image = image;
    }

}
