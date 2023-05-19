package com.example.SocialMedia1.API;


import com.example.SocialMedia1.Model.Comment;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.FavouriteModel;
import com.example.SocialMedia1.Model.FollowModel;
import com.example.SocialMedia1.Model.FollowerModel;
import com.example.SocialMedia1.Model.FollowingModel;
import com.example.SocialMedia1.Model.ImagePost;
import com.example.SocialMedia1.Model.LikeModel;
import com.example.SocialMedia1.Model.LoginModel;
import com.example.SocialMedia1.Model.Notifications;
import com.example.SocialMedia1.Model.Posts;
import com.example.SocialMedia1.Model.Profile;
import com.example.SocialMedia1.Model.RegisterModel;


import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface InterfaceAPI {


    @POST("api/getUser")
    @FormUrlEncoded
    Call<Data> getUser(
            @Field("uid") String uid);


    @GET("api/getAllUsers")
    Call<List<Data>> getAllUsers();

    @POST("api/createAuth")
    @FormUrlEncoded
    Call<RegisterModel> registerUser(
            @Field("name") String name,
            @Field("member") String member,
            @Field("email") String email,
            @Field("password") String password
            );
    @POST("api/Login")
    @FormUrlEncoded
    Call<LoginModel> loginUser(
            @Field("email") String email,
            @Field("password") String password);

    @POST("api/Follow")
    @FormUrlEncoded
    Call<FollowModel> getFollow(
            @Field("uid") String uid);

//    @GET("api/Posts")
//    Call<JsonObject> getPost();
    @GET("api/Posts")
    Call<List<Posts>> getPost();

    @POST("api/Favourite")
    @FormUrlEncoded
    Call<List<Posts>> PostFavourite(
            @Field("uid") String uid);

    @POST("api/favourite")
    @FormUrlEncoded
    Call<List<Posts>> getFavourite(
            @Field("uid") String uid);

    @POST("api/uploadProfile")
    @Multipart
    Call<String> uploadProfile(
            @Part MultipartBody.Part image);

    @POST("api/uploadPosts")
    @Multipart
    Call<String> uploadPostImage(
            @Part MultipartBody.Part image);

    @POST("api/Follow/following")
    @FormUrlEncoded
    Call<List<FollowingModel>> getFollowing(
            @Field("uid") String uid);

    @POST("api/Follow/followers")
    @FormUrlEncoded
    Call<List<FollowerModel>> getFollowers(
            @Field("uid") String uid);

    @POST("api/PostDetail")
    @FormUrlEncoded
    Call<Posts> getPostDetail(
            @Field("postid") String postid);

    @GET("api/PostCount")
    Call<Integer> PostCount();


    @POST("api/NewPost")
    @FormUrlEncoded
    Call<String> newPost(
            @Field("date") String date,
            @Field("postImage") String postImage,
            @Field("description") String description,
            @Field("publisher") String publisher,
            @Field("profile") String profile,
            @Field("memer") String memer,
            @Field("username") String username,
            @Field("counterPost") int counterPost);


    @POST("api/Follow/unFollow")
    @FormUrlEncoded
    Call<String> unFollow(
            @Field("uid") String uid,
            @Field("followid") String followid);

    @POST("api/Follow/addFollow")
    @FormUrlEncoded
    Call<String> addFollow(
            @Field("uid") String uid,
            @Field("followid") String followid);

    @POST("api/addNotifi")
    @FormUrlEncoded
    Call<String> addNotifi(
            @Field("uid") String uid, // người sẽ nhận được thông báo
            @Field("userid") String userid, // người tạo thông báo
            @Field("comment") String comment,
            @Field("postid") String postid);
//            @Field("ispost") boolean ispost);

    @POST("api/getNotifi")
    @FormUrlEncoded
    Call<List<Notifications>> getNotifi(
            @Field("uid") String uid);

    @POST("api/islike")
    @FormUrlEncoded
    Call<List<LikeModel>> isLike(
            @Field("postid") String postid);


    @POST("api/addLike")
    @FormUrlEncoded
    Call<String> addLike(
            @Field("uid") String profileid,
            @Field("postid") String postid);

    @POST("api/unLike")
    @FormUrlEncoded
    Call<String> unLike(
            @Field("uid") String profileid,
            @Field("postid") String postid);


    @POST("api/addfavourite")
    @FormUrlEncoded
    Call<String> addFavor(
            @Field("uid") String profileid,
            @Field("postid") String postid);

    @POST("api/unfavourite")
    @FormUrlEncoded
    Call<String> unFavor(
            @Field("uid") String profileid,
            @Field("postid") String postid);

    @POST("api/commentCount")
    @FormUrlEncoded
    Call<String> commentCount(
            @Field("postid") String postid);

    @POST("api/getComment")
    @FormUrlEncoded
    Call<List<Comment>> getComments(
            @Field("postid") String postid);


    @POST("api/addComment")
    @FormUrlEncoded
    Call<String> addComment(
            @Field("comment") String comment,
            @Field("publisher") String publisher,
            @Field("time") String time);


}
