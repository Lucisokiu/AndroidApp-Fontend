package com.example.SocialMedia1.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.FollowingModel;
import com.example.SocialMedia1.OthersProfile;
import SocialMedia1.R;

import com.example.SocialMedia1.Retrofit.NetworkUtil;


import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Cache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final Context context;
    List<Data> dataList;
    String profileid;

    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    public SearchAdapter(Context context, List<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

//    FirebaseUser user;
//    DatabaseReference reference;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences preferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=preferences.getString("profileid","");
//        reference = FirebaseDatabase.getInstance().getReference().child("Users");
//        user = FirebaseAuth.getInstance().getCurrentUser();
        Data data = dataList.get(position);

        holder.username.setText(data.getUsername());
        holder.memer.setText(data.getMemer());
        Glide.with(context).load(data.getProfileUrl()).centerCrop().placeholder(R.drawable.profile_image).into(holder.profile);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OthersProfile.class);
                intent.putExtra("uid", data.getUser_id());
                context.startActivity(intent);
            }
        });

//        isFollowing(data.getUser_id(), holder.btn_follow, holder.btn_following);

        if (profileid.equals(data.getUser_id())) {
            holder.btn_follow.setVisibility(View.GONE);
            holder.btn_following.setVisibility(View.GONE);
        }else
        {
            holder.btn_follow.setVisibility(View.VISIBLE);
            holder.btn_following.setVisibility(View.VISIBLE);
            isFollowing(data.getUser_id(), holder.btn_follow, holder.btn_following);

        }

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("Follow")) {
                    Call<String> call = interfaceAPI.addFollow(profileid,data.getUser_id());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
//                            Log.d("data.getUser_id()", data.getUser_id());
                                addNotifications(data.getUser_id());

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("Throwable",t.getMessage());

                        }
                    });


                    //nenu follow avutunna vaadi userid
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(user.getUid())
//                            .child("following").child(dataList.get(position).getUser_id()).setValue(true);
//
//                    addNotifications(dataList.get(position).getUser_id());
//
//                    //vaadi followers lo nenu
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(dataList.get(position).getUser_id())
//                            .child("followers").child(user.getUid()).setValue(true);

                }
            }
        });
        holder.btn_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_following.getText().toString().equals("Following")) {

                    Call<String> call = interfaceAPI.unFollow(profileid,data.getUser_id());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
//                            addNotifications(data.getUser_id());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(user.getUid())
//                            .child("following").child(dataList.get(position).getUser_id()).removeValue();
//
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(dataList.get(position).getUser_id())
//                            .child("followers").child(user.getUid()).removeValue();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView username, memer;
        Button btn_follow, btn_following;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            memer = itemView.findViewById(R.id.memer);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_following = itemView.findViewById(R.id.btn_following);


        }
    }

    private void isFollowing(final String userid, final Button follow, final Button following) {


        Call<List<FollowingModel>> call = interfaceAPI.getFollowing(profileid);
        call.enqueue(new Callback<List<FollowingModel>>() {
            @Override
            public void onResponse(Call<List<FollowingModel>> call, Response<List<FollowingModel>> response) {
                if(response.isSuccessful())
                {
                    follow.setVisibility(View.VISIBLE);
                    following.setVisibility(View.GONE);
                    for(FollowingModel followingModel : response.body()) {
                        if (userid.equals(followingModel.getId())) {
                            follow.setVisibility(View.GONE);
                            following.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FollowingModel>> call, Throwable t) {
                Toast.makeText(context , t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
//                .child(user.getUid()).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(userid).exists()) {
//                    follow.setVisibility(View.GONE);
//                    following.setVisibility(View.VISIBLE);
//                } else {
//                    follow.setVisibility(View.VISIBLE);
//                    following.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }
    private void addNotifications(String userid)
    {

        Call<String> call = interfaceAPI.addNotifi(userid,profileid,"started following you","");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Notifi added!!!","OK");

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Throwable",t.getMessage());

            }
        });

    }
//        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid);
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
