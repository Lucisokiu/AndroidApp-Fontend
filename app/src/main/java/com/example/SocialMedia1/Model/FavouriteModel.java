package com.example.SocialMedia1.Model;

import com.google.gson.annotations.SerializedName;

public class FavouriteModel {
    @SerializedName("idposts")
    String idposts;

    public FavouriteModel(String idposts) {
        this.idposts = idposts;
    }
    public String getIdposts() {
        return idposts;
    }

    public void setIdposts(String idposts) {
        this.idposts = idposts;
    }
}
