package com.example.SocialMedia1.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.Notifications;
import com.example.SocialMedia1.Model.Posts;
import com.example.SocialMedia1.OthersProfile;
import com.example.SocialMedia1.PostDetails;
import SocialMedia1.R;

import com.example.SocialMedia1.Retrofit.NetworkUtil;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<Notifications> list;
    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);
    public NotificationAdapter(Context context, List<Notifications> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Notifications notifications=list.get(position);
        holder.comment.setText(notifications.getComment());
        getUserInfo(holder.profile,holder.username,notifications.getUserid());

        if (notifications.isIspost())
        {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image,notifications.getPostid());
        }else
        {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifications.isIspost())
                {
                    Intent intent=new Intent(context, PostDetails.class);
                    intent.putExtra("postid",notifications.getPostid());
                    context.startActivity(intent);
                }else
                {
                    Intent intent=new Intent(context, OthersProfile.class);
                    intent.putExtra("uid",notifications.getUserid());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView post_image;
        CircleImageView profile;
        TextView username,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);



            post_image=itemView.findViewById(R.id.post_image);
            profile=itemView.findViewById(R.id.profile_image);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);

        }
    }
    private void getUserInfo(final ImageView imageView,TextView username,String userid)
    {
        Call<Data> call = interfaceAPI.getUser(userid);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if(response.isSuccessful())
                {
                    username.setText(response.body().getUsername());
                    Picasso.get().load(response.body().getProfileUrl()).into(imageView);

                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }
//    private void getUserInfo(final ImageView imageView,TextView username,String userid)
//    {
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
//        reference.child(userid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Data data=snapshot.getValue(Data.class);
//
//                username.setText(data.getUsername());
//                Picasso.get().load(data.getProfileUrl()).into(imageView);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
private void getPostImage(final ImageView imageView,String postid)
{
    Call<Posts> call = interfaceAPI.getPostDetail(postid);
    call.enqueue(new Callback<Posts>() {
        @Override
        public void onResponse(Call<Posts> call, Response<Posts> response) {
                Picasso.get().load(response.body().getPostImage()).into(imageView);
        }

        @Override
        public void onFailure(Call<Posts> call, Throwable t) {
            Log.e("Error", t.getMessage());
        }
    });
}
//    private void getPostImage(final ImageView imageView,String postid)
//    {
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Posts");
//        reference.child(postid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Posts posts=snapshot.getValue(Posts.class);
//
//                Picasso.get().load(posts.getPostImage()).into(imageView);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}
