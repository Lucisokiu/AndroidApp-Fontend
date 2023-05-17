package com.example.SocialMedia1.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.PostAdapter;
import com.example.SocialMedia1.Model.Posts;
import SocialMedia1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {

    RecyclerView recyclerView;
    List<Posts> postsList;
    PostAdapter adapter;


    FirebaseAuth auth;
    //    FirebaseUser user;
    String profileid;
//    DatabaseReference reference,postRef;

    List<Posts> postRef;
    //API
    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        SharedPreferences preferences = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = preferences.getString("uid","");
        Log.d("uid_feed", profileid);



        postsList = new ArrayList<>();
        adapter = new PostAdapter(getActivity(), postsList);
        recyclerView.setAdapter(adapter);


        getPosts();




        return view;
    }
        private void getPosts(){
        Call<List<Posts>> call = interfaceAPI.getPost();
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
//                for ( Posts posts : response.body()) {
//                    postsList.add(posts);
//                }
                postsList.addAll(response.body());
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                    Log.e("False", t.getMessage());

            }
        });

    }



//    hàm lấy xử lý reponse object
//    private void getPosts(){
//            Call<JsonObject> call = interfaceAPI.getPost();
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    if (response.isSuccessful()) {
//                        JsonObject jsonObject = response.body();
//                        try {
//                            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
//                                JsonObject postObject = entry.getValue().getAsJsonObject();
//
//
//                                String date = postObject.get("date").getAsString();
//                                String postid = postObject.get("postid").getAsString();
//                                String postImage = postObject.get("postImage").getAsString();
//                                String description = postObject.get("description").getAsString();
//                                String publisher = postObject.get("publisher").getAsString();
//                                String profile = postObject.get("profile").getAsString();
//                                String memer = postObject.get("memer").getAsString();
//                                String username = postObject.get("username").getAsString();
//                                long counterPost = postObject.get("counterPost").getAsLong();
//                                // Tạo đối tượng Posts từ thông tin name và age
//                                Posts post = new Posts(date, postid,postImage,description,publisher,profile,memer,username, counterPost );
//                                // Thêm post vào danh sách postsList
//                                postsList.add(post);
//                            }
//                            adapter.notifyDataSetChanged();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//              }

//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    Log.e("False", t.getMessage());
//                }
//            });
//        }





//    private void getPosts()
//    {
//        Query query= postRef.orderByChild("counterPost");
//        Query query = postRef;
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postsList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    Posts posts=dataSnapshot.getValue(Posts.class);
//                    if (posts.getCounterPost()>=0)
//                    {
//                        postsList.add(posts);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                Toast.makeText(getContext(), "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}



