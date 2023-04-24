package com.example.SocialMedia1.Model;

import com.google.gson.annotations.SerializedName;

public class LikeModel {
    @SerializedName("postid")
    String postid;

    public LikeModel(String postid) {
        this.postid = postid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
