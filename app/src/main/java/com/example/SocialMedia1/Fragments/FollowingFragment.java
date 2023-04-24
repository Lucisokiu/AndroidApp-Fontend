package com.example.SocialMedia1.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.PostAdapter;
import com.example.SocialMedia1.Model.FollowModel;
import com.example.SocialMedia1.Model.FollowingModel;
import com.example.SocialMedia1.Model.Posts;
import com.example.SocialMedia1.NotificationActivity;
import com.example.SocialMedia1.OthersProfile;
import com.example.SocialMedia1.PostActivity;
import SocialMedia1.R;

import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.example.SocialMedia1.SearchUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FollowingFragment extends Fragment {

    String profileid;

    RecyclerView recyclerView;
    List<Posts> postsList;
    PostAdapter adapter;

    CircleImageView profile;
    ImageView search;
    ImageView post_one, note, active;
    TextView no;
    Button discover;


    FirebaseAuth auth;
    FirebaseUser user;

    DatabaseReference reference;

    List<String> followingList;

    private NetworkUtil networkUtil;
    private Retrofit retrofit;
    private InterfaceAPI interfaceAPI;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);

//        Bundle bundle = getArguments();
//        uid = bundle.getString("uid");
//        Log.d("uid", uid);

        SharedPreferences prefs = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "");

        profile = view.findViewById(R.id.profile_image);
        search = view.findViewById(R.id.search);
        post_one = view.findViewById(R.id.post_one);
        no = view.findViewById(R.id.no);
        discover = view.findViewById(R.id.discover);
        note = view.findViewById(R.id.note);
        active = view.findViewById(R.id.active);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        postsList = new ArrayList<>();
        adapter = new PostAdapter(getActivity(), postsList);

        checkFollowing();

        recyclerView.setAdapter(adapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchUsers.class);
                startActivity(intent);
            }
        });
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });


        reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("áds" , snapshot.toString());
                    String p = snapshot.child("profileUrl").getValue().toString();

                    Picasso.get().load(p).placeholder(R.drawable.profile_image).into(profile);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        clicks();
//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), OthersProfile.class);
//                Bundle bundle = getActivity().getIntent().getExtras();
//                String uid = bundle.getString("uid");
//                intent.putExtra("uid", uid);
//                startActivity(intent);
//            }
//        });


        active.setVisibility(View.GONE);


        return view;
    }


    private void clicks() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchUsers.class));
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchUsers.class));
            }
        });


        post_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OthersProfile.class);
                Bundle bundle = getActivity().getIntent().getExtras();
                String uid = bundle.getString("uid");
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });


    }

    private void checkFollowing() {
        Log.d("uid", profileid);
        networkUtil = new NetworkUtil();
        retrofit = networkUtil.getRetrofit();
        interfaceAPI = retrofit.create(InterfaceAPI.class);

        followingList = new ArrayList<>();

        Call<FollowModel> followModelCall = interfaceAPI.getFollow(profileid);
        followModelCall.enqueue(new Callback<FollowModel>() {
            @Override
            public void onResponse(Call<FollowModel> call, Response<FollowModel> response) {
                if (response.isSuccessful()) {
                    FollowModel followModel = response.body();
                    Log.d("followModel", followModel.toString());
//                    String following = followModel;
//                    Log.d("following ", following);
                    FollowingModel followingModel = followModel.getFollowing();

                    followingList.add(followingModel.getId());
                    getPosts();
                    no.setVisibility(View.GONE);
                    discover.setVisibility(View.GONE);
                } else {
                    no.setVisibility(View.VISIBLE);
                    discover.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onFailure(Call<FollowModel> call, Throwable t) {
                Log.d("lỗi login", "đây : " + t.getMessage());

            }
        });


//
//        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(user.getUid()).child("following");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        followingList.add(dataSnapshot.getKey());
//                    }
//                    getPosts();
//
//                    no.setVisibility(View.GONE);
//                    discover.setVisibility(View.GONE);
//                }else
//                {
//                    no.setVisibility(View.VISIBLE);
//                    discover.setVisibility(View.VISIBLE);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }


    private void getPosts(){
        Call<List<Posts>> call = interfaceAPI.getPost();
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
//                for ( Posts posts : response.body()) {
//                    postsList.add(posts);
//                }
                for (String id : followingList) {
                    for ( Posts posts : response.body()) {
                        if (posts.getPublisher().equals(id)) {

                            postsList.add(posts);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Log.e("False", t.getMessage());

            }
        });

    }

//      hàm xử lý reponse object
//    private void getPosts() {
//        Call<JsonObject> call = interfaceAPI.getPost();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful()) {
//                    JsonObject jsonObject = response.body();
//                    try {
//                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
//                            JsonObject postObject = entry.getValue().getAsJsonObject();
//
//
//                            String date = postObject.get("date").getAsString();
//                            String postid = postObject.get("postid").getAsString();
//                            String postImage = postObject.get("postImage").getAsString();
//                            String description = postObject.get("description").getAsString();
//                            String publisher = postObject.get("publisher").getAsString();
//                            String profile = postObject.get("profile").getAsString();
//                            String memer = postObject.get("memer").getAsString();
//                            String username = postObject.get("username").getAsString();
//                            long counterPost = postObject.get("counterPost").getAsLong();
//                            // Tạo đối tượng Posts từ thông tin name và age
//                            Posts post = new Posts(date, postid, postImage, description, publisher, profile, memer, username, counterPost);
//                            for (String id : followingList) {
//                                if (post.getPublisher().equals(id)) {
//
//                                    postsList.add(post);
//                                }
//                            }
//                            // Thêm post vào danh sách postsList
//                        }
//                        adapter.notifyDataSetChanged();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("False", t.getMessage());
//            }
//        });
//    }


//        DatabaseReference postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
//        Query query=postRef.orderByChild("counterPost");
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postsList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    Posts posts=dataSnapshot.getValue(Posts.class);
//
//                    for (String id : followingList)
//                    {
//                        if (posts.getPublisher().equals(id))
//                        {
//
//                            postsList.add(posts);
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


}