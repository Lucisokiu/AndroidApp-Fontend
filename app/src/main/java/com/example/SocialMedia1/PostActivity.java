package com.example.SocialMedia1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.ImagePost;
import com.example.SocialMedia1.Model.Posts;
import com.example.SocialMedia1.Model.Profile;
import com.example.SocialMedia1.RealPathUtil.RealPathUtil;
import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import SocialMedia1.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostActivity extends AppCompatActivity {
    ImageView cancel, pick_image;
    TextView post;
    EditText description;
    CardView post_card;
    ProgressDialog progressDialog;

    //    profileUrl,username,memer
    String post_url;
    Uri postUri;
    String RealpostUri;



    private int counterPost;
    //API
    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);
    String profileid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = preferences.getString("profileid","");

        init();




        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostActivity.this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

//        getData();

        pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, HomeActivity.class));
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                postUri = data.getData();
                pick_image.setImageURI(postUri);
                pick_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }

        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }
    }

        private void uploadPost() {
            progressDialog = new ProgressDialog(PostActivity.this);
            progressDialog.setTitle("New Post");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            if (postUri == null) {
                Toast.makeText(this, "No Meme select!!", Toast.LENGTH_SHORT).show();
            } else {
                // Tạo request body với định dạng form data
                RealpostUri = RealPathUtil.getRealPath(this, postUri);
                File imageFile = new File(RealpostUri);
                String idpost = System.currentTimeMillis()+"."+fileExtension(postUri);
                RequestBody requestBody = RequestBody.create(MediaType.parse(fileExtension(postUri)), imageFile);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", idpost, requestBody);

                // Gửi request đến server bằng Retrofit
                Call<String> call = interfaceAPI.uploadPostImage(imagePart);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                            progressDialog.dismiss();
                            post_url = response.body();
                            Log.d("Post URL", post_url);
                            savePostsDataInFirebase(post_url);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Error"+t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Post URL Fail", t.getMessage());

                    }
                });
            }
        }

    private void init()
    {
        cancel=findViewById(R.id.cancel_post);
        pick_image=findViewById(R.id.pick);
        post=findViewById(R.id.post_upload);
        pick_image=findViewById(R.id.pick);
        post_card=findViewById(R.id.post_card);
        description=findViewById(R.id.post_description);

    }

    public String fileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap map=MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void savePostsDataInFirebase(final String url) {
        Log.d("url post" , url);
        Call<Integer>  call = interfaceAPI.PostCount();
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful())
                {
                    counterPost=response.body();

                }else
                {
                    counterPost=0;
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
        Call<Data> getUser = interfaceAPI.getUser(profileid);
        getUser.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if(response.isSuccessful())
                {
                    String username = response.body().getUsername();
                    String member = response.body().getMemer();
                    String profileUrl = response.body().getProfileUrl();
                    Date date=new Date();
                    SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm a");
                    String currentDate=format.format(date);

                    Call<String> newPost = interfaceAPI.newPost(currentDate,url,description.getText().toString(),profileid,profileUrl,member,username,counterPost);
                    newPost.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "New post added!!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PostActivity.this,HomeActivity.class));
                        Log.d("New post added!!","Success");
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(PostActivity.this,HomeActivity.class));
                            Log.d("New post!!",t.getMessage());

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Toast.makeText(PostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("New post!!",t.getMessage());

            }
        });

    };

    
    
}