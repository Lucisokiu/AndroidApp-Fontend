package com.example.SocialMedia1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.ShowAdapter;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.FollowerModel;
import com.example.SocialMedia1.Model.FollowingModel;
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

public class ShowList extends AppCompatActivity {

    String id;
    String title;
    TextView title_tv;
    List<String> list;

    Toolbar toolbar;

    RecyclerView recyclerView;
    ShowAdapter adapter;
    List<Data> data;
    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        title_tv=findViewById(R.id.title);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");
        Bundle bundle = intent.getBundleExtra("myBundle");
        list = bundle.getStringArrayList("myList");


        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title_tv.setText(title);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        data=new ArrayList<>();
        adapter=new ShowAdapter(this,data);
        recyclerView.setAdapter(adapter);

        list=new ArrayList<>();

//        switch (title)
//        {
//            case "Followers":
//                getFollowers();
//                break;
//            case "Following":
//                getFollowing();
//                break;
//        }

        showUsers();

    }
    private void getFollowers() {

        Call<List<FollowerModel>> callfollowers = interfaceAPI.getFollowers(id);
        callfollowers.enqueue(new Callback<List<FollowerModel>>() {
            @Override
            public void onResponse(Call<List<FollowerModel>> call, Response<List<FollowerModel>> response) {
                list.clear();
                for ( FollowerModel followerModel : response.body())
                {
                    list.add(followerModel.getId());
                }
                showUsers();

            }

            @Override
            public void onFailure(Call<List<FollowerModel>> call, Throwable t) {
                Toast.makeText(ShowList.this, "Error loading..", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getFollowing() {

        Call<List<FollowingModel>> callfollowers = interfaceAPI.getFollowing(id);
        callfollowers.enqueue(new Callback<List<FollowingModel>>() {
            @Override
            public void onResponse(Call<List<FollowingModel>> call, Response<List<FollowingModel>> response) {
                list.clear();
                for ( FollowingModel followingModel : response.body())
                {
                    list.add(followingModel.getId());
                }
                showUsers();

            }

            @Override
            public void onFailure(Call<List<FollowingModel>> call, Throwable t) {
                Toast.makeText(ShowList.this, "Error loading..", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private void getFollowers() {
//
//
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow")
//                .child(id).child("followers");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    list.add(dataSnapshot.getKey());
//                }
//                showUsers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                Toast.makeText(ShowList.this, "Error loading..", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//
//
//
//
//
//    }



//    private void getFollowing() {
//
//
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow")
//                .child(id).child("following");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    list.add(dataSnapshot.getKey());
//                }
//                showUsers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                Toast.makeText(ShowList.this, "Error loading..", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//
//
//
//
//
//    }



    private void showUsers()
    {
        Call<List<Data>> callData = interfaceAPI.getAllUsers();
        callData.enqueue(new Callback<List<Data>>() {
            @Override
            public void onResponse(Call<List<Data>> call, Response<List<Data>> response) {
                data.clear();
                for(Data user : response.body())
                {
                    for(String id : list) {
                        if (user.getUser_id().equals(id)) {
                            data.add(user);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<Data>> call, Throwable t) {

            }
        });
    }
//    private void showUsers()
//    {
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                data.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    Data user=dataSnapshot.getValue(Data.class);
//                    for (String id : list)
//                    {
//                        if (user.getUser_id().equals(id))
//                        {
//                            data.add(user);
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}