package com.example.SocialMedia1.Model;

import com.google.gson.annotations.SerializedName;

public class Notifications {
    @SerializedName("userid")
    String userid;
    @SerializedName("comment")
    String comment;
    @SerializedName("postid")
    String postid;
    @SerializedName("ispost")
    boolean ispost;

    public Notifications(String userid, String comment, String postid, boolean ispost) {
        this.userid = userid;
        this.comment = comment;
        this.postid = postid;
        this.ispost = ispost;
    }

    public Notifications(){}

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
