package com.example.SocialMedia1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.CommentAdapter;
import com.example.SocialMedia1.Model.Comment;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import SocialMedia1.R;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentActivity extends AppCompatActivity {

    CircleImageView profile;
    EditText edit_comment;
    ImageView send;
    Toolbar toolbar;
    TextView no;

    RecyclerView recyclerView;
    List<Comment> commentList;
    CommentAdapter adapter;


    String postid;
    String publisher;
    String profileid;

    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        profile = findViewById(R.id.profile_image);
        edit_comment = findViewById(R.id.comment_edit);
        send = findViewById(R.id.send);
        no = findViewById(R.id.no);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisher = intent.getStringExtra("publisher");
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = preferences.getString("profileid","");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        commentList = new ArrayList<>();
        adapter = new CommentAdapter(this, commentList);

        getComments();

        recyclerView.setAdapter(adapter);

        getImage();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_comment.getText().toString().isEmpty()) {
                    Toast.makeText(CommentActivity.this, "Comment can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });


    }

    private void getComments() {
        Call<List<Comment>> call = interfaceAPI.getComments(postid);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful())
                {
                    commentList.clear();
                    commentList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    no.setVisibility(View.GONE);
                }else
                {
                    no.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(CommentActivity.this, "Error while load comments", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void addComment() {

        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm a");
        String currentDate=format.format(date);

        Call<String> call = interfaceAPI.addComment(edit_comment.getText().toString() , publisher ,  currentDate);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                addNotifications();
                edit_comment.setText("");

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CommentActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void addNotifications()
    {
        Call<String> call = interfaceAPI.addNotifi(publisher,profileid,edit_comment.getText().toString(),postid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Success","OK");

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Throwable",t.getMessage());

            }
        });
    }

        private void getImage(){
            Call<Data> call = interfaceAPI.getUser(profileid);
            call.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call, Response<Data> response) {


                Picasso.get().load(response.body().getProfileUrl()).into(profile);

                }

                @Override
                public void onFailure(Call<Data> call, Throwable t) {
                    Toast.makeText(CommentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

}


}
