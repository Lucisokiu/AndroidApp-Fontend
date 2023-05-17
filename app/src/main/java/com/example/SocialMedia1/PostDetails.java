package com.example.SocialMedia1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.PostAdapter;
import com.example.SocialMedia1.Model.Posts;
import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import SocialMedia1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Posts> postsList;
    PostAdapter adapter;

    String postid;

    //API
    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        postsList = new ArrayList<>();
        adapter = new PostAdapter(this, postsList);

        postid = getIntent().getStringExtra("postid");

        recyclerView.setAdapter(adapter);


        readPosts();


    }
    private void readPosts() {
        Call<Posts> postsCall = interfaceAPI.getPostDetail(postid);
        postsCall.enqueue(new Callback<Posts>() {
            @Override
            public void onResponse(Call<Posts> call, Response<Posts> response) {
                postsList.clear();
                postsList.add(response.body());
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t) {
                Toast.makeText(PostDetails.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
}


}