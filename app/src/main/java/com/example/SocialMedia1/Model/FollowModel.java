package com.example.SocialMedia1.Model;

import com.google.gson.annotations.SerializedName;

public class FollowModel {
    @SerializedName("uid")
    String uid;
    @SerializedName("followers")
    FollowerModel followers;
    @SerializedName("following")
    FollowingModel following;

    public FollowModel(String uid, FollowerModel followers, FollowingModel following) {
        this.uid = uid;
        this.followers = followers;
        this.following = following;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public FollowerModel getFollowers() {
        return followers;
    }

    public void setFollowers(FollowerModel followers) {
        this.followers = followers;
    }

    public FollowingModel getFollowing() {
        return following;
    }

    public void setFollowing(FollowingModel following) {
        this.following = following;
    }
}
