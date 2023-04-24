package com.example.SocialMedia1.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.PhotosAdapter;
import com.example.SocialMedia1.Model.FavouriteModel;
import com.example.SocialMedia1.Model.Posts;
import SocialMedia1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {

    private List<String> mySaves;
    RecyclerView recyclerView_save;
    List<Posts> mPost_save;
    PhotosAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    //API
    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    String profileid;
    List<FavouriteModel> favouriteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        NetworkUtil networkUtil = new NetworkUtil();
        Retrofit retrofit = networkUtil.getRetrofit();
        InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_favourite2, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        profileid = sharedPreferences.getString("uid", "");
        Log.d("uid-favourite" ,profileid);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference().child("Users");


        recyclerView_save=view.findViewById ( R.id.recyclerView );
        recyclerView_save.setHasFixedSize ( true );
        recyclerView_save.setLayoutManager(new GridLayoutManager(getContext(),3));

        mPost_save=new ArrayList<>(  );
        adapter=new PhotosAdapter ( getContext (),mPost_save );
        recyclerView_save.setAdapter ( adapter );

        Saved();
        return view;
    }

    private void Saved() {
        mySaves = new ArrayList<>();

        Call<List<FavouriteModel>> getFav = interfaceAPI.getFavourite(profileid);
        getFav.enqueue(new Callback<List<FavouriteModel>>() {
            @Override
            public void onResponse(Call<List<FavouriteModel>> call, Response<List<FavouriteModel>> response) {
                if(response.isSuccessful()) {
                    favouriteList = response.body();
                    Log.d("favouriteModelList:", favouriteList.toString());
                    readSaves();

                }

            }

            @Override
            public void onFailure(Call<List<FavouriteModel>> call, Throwable t) {
                Log.e("Lỗi-Favouritèagment-Saved()", t.getMessage());
            }
        });

    }
//        DatabaseReference reference=FirebaseDatabase.getInstance ().getReference ().child ( "Favourites" )
//                .child ( user.getUid () );
//        reference.addValueEventListener ( new ValueEventListener () {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren ()){
//                    mySaves.add ( snapshot.getKey () );
//                }
//                readSaves();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        } );
//    }




    private void readSaves() {
            Call<List<Posts>> listPost = interfaceAPI.getPost();
            listPost.enqueue(new Callback<List<Posts>>() {
                @Override
                public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                    if (response.isSuccessful()) {
                        for (Posts posts : response.body()) {
                            for (String id : mySaves) {
                                if (posts.getPostid().equals(id)) {
                                    mPost_save.add(posts);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }
                }
                @Override
                public void onFailure(Call<List<Posts>> call, Throwable t) {
                    Log.e("Lỗi-Favouritèagment-readSaves():", t.getMessage());
                }
            });
    }
//        DatabaseReference reference= FirebaseDatabase.getInstance ().getReference ().child ( "Posts" );
//
//        reference.addValueEventListener ( new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mPost_save.clear ();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren ()){
//                    Posts post=snapshot.getValue (Posts.class);
//
//                    for (String id : mySaves){
//                        if (post.getPostid ().equals ( id )){
//                            mPost_save.add ( post );
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged ();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        } );
//    }



}