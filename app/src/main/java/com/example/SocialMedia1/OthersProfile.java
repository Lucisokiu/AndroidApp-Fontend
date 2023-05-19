package com.example.SocialMedia1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.PhotosAdapter;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.FollowerModel;
import com.example.SocialMedia1.Model.FollowingModel;
import com.example.SocialMedia1.Model.Posts;
import com.example.SocialMedia1.Retrofit.NetworkUtil;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import SocialMedia1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OthersProfile extends AppCompatActivity {

    List<String> listfollowing;
    List<String> listfollowers;

    Button btn_follow,btn_following;
    ImageView profile;
    ImageView bg;
    TextView username,memer,following_count,followers_count,pos_count;
    TextView followers,following;

    Toolbar toolbar;

    RecyclerView recyclerView;
    List<Posts> postsList;
    PhotosAdapter adapter;


    String id;

    String profileid;

    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences preferences=getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=preferences.getString("profileid","");
        init();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        id=getIntent().getStringExtra("uid");

        if (id.equals(profileid))
        {
            btn_follow.setVisibility(View.VISIBLE);
            btn_follow.setText("Settings");
        }else
        {
            checkFollow();
        }

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));

        postsList=new ArrayList<>();
        adapter=new PhotosAdapter(this,postsList);
        listfollowing=new ArrayList<>();
        listfollowers=new ArrayList<>();
        getImages();
        recyclerView.setAdapter(adapter);


        getUserData();
        getFollowCount();

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("myList", (ArrayList<String>) listfollowers);
                Intent intent=new Intent(OthersProfile.this, ShowList.class);
                intent.putExtra("id",profileid);
                intent.putExtra("myBundle", bundle);
                intent.putExtra("title",followers.getText().toString());
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("myList", (ArrayList<String>) listfollowing);
                Intent intent=new Intent(OthersProfile.this, ShowList.class);
                intent.putExtra("id",profileid);
                intent.putExtra("myBundle",bundle);
                intent.putExtra("title",following.getText().toString());
                startActivity(intent);
            }
        });
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_follow.getText().toString().equals("Follow"))
                {
                    Call<String> addFollow = interfaceAPI.addFollow(profileid,id);
                    addFollow.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                                addNotifications();

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });

                }
            }
        });
        btn_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_following.getText().toString().equals("Following"))
                {
                    Call<String> unfollow = interfaceAPI.unFollow(profileid,id);
                    unfollow.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            addNotifications();

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            }
        });

//        btn_following.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (btn_following.getText().toString().equals("Following"))
//                {
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(user.getUid())
//                            .child("following").child(id).removeValue();
//
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(id)
//                            .child("followers").child(user.getUid()).removeValue();
//                }
//            }
//        });





    }
    private void init()
    {
        btn_follow=findViewById(R.id.btn_follow);
        btn_following=findViewById(R.id.btn_following);
        profile=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        memer=findViewById(R.id.memer);
        bg=findViewById(R.id.background);
        following_count=findViewById(R.id.following_count);
        followers_count=findViewById(R.id.followers_count);
        pos_count=findViewById(R.id.posts);
        followers=findViewById(R.id.followers);
        following=findViewById(R.id.following);



    }
    private void getUserData()
    {
        Log.d("profileid",id);

        Call<Data> call = interfaceAPI.getUser(id);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Log.d("",response.body().toString());
                String n= response.body().getUsername();
                String m= response.body().getMemer();
                String p= response.body().getProfileUrl();
                String b= response.body().getBackground();



                username.setText(n);
                memer.setText(m);
                Picasso.get().load(p).placeholder(R.drawable.profile_image).into(profile);
                Picasso.get().load(b).into(bg);


            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Toast.makeText(OthersProfile.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//    private void getUserData()
//    {
//
//        reference.child(id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                String n=snapshot.child("username").getValue().toString();
//                String m=snapshot.child("memer").getValue().toString();
//                String p=snapshot.child("profileUrl").getValue().toString();
//                String b=snapshot.child("background").getValue().toString();
//
//
//                username.setText(n);
//                memer.setText(m);
//                Picasso.get().load(p).placeholder(R.drawable.profile_image).into(profile);
//                Picasso.get().load(b).into(bg);
//
//
////                Glide.with(getActivity()).load(p).centerCrop().placeholder(R.drawable.profile_image).into(profile);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(OthersProfile.this, "Error", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }
private void getFollowCount()
{
    Call<List<FollowingModel>> followingModelCall = interfaceAPI.getFollowing(id);
    followingModelCall.enqueue(new Callback<List<FollowingModel>>() {
        @Override
        public void onResponse(Call<List<FollowingModel>> call, Response<List<FollowingModel>> response) {
            int count = response.body().size();
            following_count.setText(""+count);
            listfollowing.clear();
            for ( FollowingModel FollowingModel : response.body())
            {
                listfollowing.add(FollowingModel.getId());
            }
        }

        @Override
        public void onFailure(Call<List<FollowingModel>> call, Throwable t) {

        }
    });


    Call<List<FollowerModel>> followersCall = interfaceAPI.getFollowers(id);
    followersCall.enqueue(new Callback<List<FollowerModel>>() {
        @Override
        public void onResponse(Call<List<FollowerModel>> call, Response<List<FollowerModel>> response) {
            int count = response.body().size();
            followers_count.setText(""+count);
            listfollowers.clear();
            for ( FollowerModel FollowersModel : response.body())
            {
                listfollowers.add(FollowersModel.getId());
            }
        }

        @Override
        public void onFailure(Call<List<FollowerModel>> call, Throwable t) {

        }
        //     đếm số lượng bài đăng -> chuyển xuống getImage để mỗi khi lấy bài post sẽ đếm luôn số lượng. khỏi phải call API 2 lần
        //        => tăng hiệu suất
    });
    //        Call<List<Posts>> callPost = interfaceAPI.getPost();
//        callPost.enqueue(new Callback<List<Posts>>() {
//            @Override
//            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
//                int i = 0;
//                for(Posts post : response.body())
//                {
//                    if (post.getPublisher().equals(profileid))
//                    {
//                        i++;
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<List<Posts>> call, Throwable t) {
//
//            }
//        });
}
//    private void getFollowCount()
//    {
//        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Follow")
//                .child(id).child("followers");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                followers_count.setText(""+snapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(OthersProfile.this, "Error", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Follow")
//                .child(id).child("following");
//        databaseReference1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                following_count.setText(""+snapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(OthersProfile.this, "Error", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        DatabaseReference postCount=FirebaseDatabase.getInstance().getReference().child("Posts");
//        postCount.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int i=0;
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    String p=dataSnapshot.child("publisher").getValue().toString();
//                    if (p.equals(id))
//                    {
//                        i++;
//                    }
//                }
//                pos_count.setText("Posts "+"("+i+")");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                Toast.makeText(OthersProfile.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }

    private void checkFollow()
    {
        Call<List<FollowingModel>> call = interfaceAPI.getFollowing(profileid);
        call.enqueue(new Callback<List<FollowingModel>>() {
            @Override
            public void onResponse(Call<List<FollowingModel>> call, Response<List<FollowingModel>> response) {
                if (response.isSuccessful()) {
                    List<FollowingModel> followingList = response.body();
                    for (FollowingModel following : followingList) {
                        if (following.getId().equals(id)) {
                            btn_follow.setVisibility(View.GONE);
                            btn_following.setVisibility(View.VISIBLE);
                        } else {
                            btn_following.setVisibility(View.GONE);
                            btn_follow.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FollowingModel>> call, Throwable t) {

            }
        });
    }
//    private void checkFollow()
//    {
//        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Follow")
//                .child(user.getUid()).child("following");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(id).exists())
//                {
//                    btn_follow.setVisibility(View.GONE);
//                    btn_following.setVisibility(View.VISIBLE);
//                }else
//                {
//                    btn_following.setVisibility(View.GONE);
//                    btn_follow.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void getImages()
    {
        Call<List<Posts>> call = interfaceAPI.getPost();
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                postsList.clear();
                int i = 0;
                for ( Posts posts : response.body()) {
                    if (posts.getPublisher().equals(id)) {

                        postsList.add(posts);
                        i++;
                    }
                    Collections.reverse(postsList);
                    adapter.notifyDataSetChanged();
                    pos_count.setText("Posts "+"("+i+")");

                }

            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Log.e("False", t.getMessage());

            }
        });
    }
//    private void getImages()
//    {
//        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Posts");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postsList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    Posts posts=dataSnapshot.getValue(Posts.class);
//                    if (posts.getPublisher().equals(id))
//                    {
//                        postsList.add(posts);
//                    }
//                }
//                Collections.reverse(postsList);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                Toast.makeText(OthersProfile.this, "Error", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    private void addNotifications()
    {
        Call<String> call = interfaceAPI.addNotifi(id,profileid,"started following you","");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    Log.d("Success","OK");

                    Toast.makeText(OthersProfile.this,response.body(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(OthersProfile.this,t.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
//    private void addNotifications()
//    {
//        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notifications").child(id);
//
//        HashMap<String,Object> map=new HashMap<>();
//
//        map.put("userid",user.getUid());
//        map.put("comment","started following you");
//        map.put("postid","");
//        map.put("ispost",false);
//
//        reference.push().setValue(map);
//    }






}