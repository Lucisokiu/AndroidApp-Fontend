package com.example.SocialMedia1.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.CommentActivity;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.FavouriteModel;
import com.example.SocialMedia1.Model.LikeModel;
import com.example.SocialMedia1.Model.Posts;
import com.example.SocialMedia1.OthersProfile;
import SocialMedia1.R;

import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<Posts> posts;
    String profileid;
    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    public PostAdapter(Context context, List<Posts> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




        SharedPreferences preferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=preferences.getString("profileid","");

//        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        Posts list=posts.get(position);

        Picasso.get().load(list.getPostImage()).into(holder.post_image);
//        String timeAgo=calTime(list.getDate());

        holder.date.setText(list.getDate());
        holder.username.setText(list.getUsername());
        holder.memer.setText(list.getMemer());
//        holder.like.setText(list.getMemer());
        Picasso.get().load(list.getProfile()).placeholder(R.drawable.profile_image).into(holder.profile);

        if (list.getDescription().isEmpty())
        {
            holder.description.setVisibility(View.GONE);
        }else
        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(list.getDescription());
        }


        publsh(holder.profile,holder.username,holder.memer,list.getPublisher());



        isLiked(list.getPostid(),holder.like);
        getLikesCount(holder.likes_count,list.getPostid());

        isSaved(list.getPostid(),holder.save);

        getCommentsCount(holder.comments_count,list.getPostid());

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,v);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.copy:

                                return true;
                            case R.id.delete:

                                // chua lam
//                                FirebaseDatabase.getInstance().getReference().child("Posts")
//                                        .child(list.getPostid()).removeValue()
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful())
//                                                {
//                                                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
//                                                }else
//                                                {
//                                                    Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });

                                return true;

                            case R.id.unfollow:
                                Call<String> call = interfaceAPI.unFollow(profileid,list.getPublisher());
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if(response.isSuccessful())
                                        {
                                            Toast.makeText(context, "unFollow!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
//                                FirebaseDatabase.getInstance().getReference().child("Follow")
//                                        .child(profileid)
//                                        .child("following").child(list.getPublisher()).removeValue();
//
//                                FirebaseDatabase.getInstance().getReference().child("Follow")
//                                        .child(list.getPublisher())
//                                        .child("followers").child(profileid).removeValue();
                                return true;
                        }
                        return true;
                    }
                });

                popupMenu.inflate(R.menu.options);
                if (!list.getPublisher().equals(profileid))
                {
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("Like"))
                {
//                    FirebaseDatabase.getInstance().getReference().child("Likes")
//                            .child(list.getPostid())
//                            .child(profileid).setValue(true);
                    Call<String> call = interfaceAPI.addLike(profileid,list.getPostid());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            addNotifications(list.getPublisher(),list.getPostid());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("Throwable",t.getMessage());

                        }
                    });
                }else
                {
                    Call<String> call = interfaceAPI.unLike(list.getPublisher(),list.getPostid());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
//                    FirebaseDatabase.getInstance().getReference().child("Likes")
//                            .child(list.getPostid())
//                            .child(profileid).removeValue();
                }
            }
        });


        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save"))
                {
                    Call<String> call = interfaceAPI.addFavor(profileid,list.getPostid());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
//                    FirebaseDatabase.getInstance().getReference().child("Favourites")
//                            .child(profileid)
//                            .child(list.getPostid())
//                            .setValue(true);
                }else
                {
                    Call<String> call = interfaceAPI.unFavor(profileid,list.getPostid());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
//                    FirebaseDatabase.getInstance().getReference().child("Favourites")
//                            .child(profileid)
//                            .child(list.getPostid())
//                            .removeValue();
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareItem(list.getPostImage(),holder.pd);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CommentActivity.class);
                intent.putExtra("postid",list.getPostid());
                intent.putExtra("publisher",list.getPublisher());
                context.startActivity(intent);
            }
        });


        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfile.class);
                intent.putExtra("uid",list.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfile.class);
                intent.putExtra("uid",list.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.memer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfile.class);
                intent.putExtra("uid",list.getPublisher());
                context.startActivity(intent);
            }
        });




    }

//    private String calTime(String date)  {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+"5:30"));
//        try {
//            long time = sdf.parse(date).getTime();
//            long now = System.currentTimeMillis();
//            CharSequence ago =
//                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
//            return ago+"";
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return "";
////        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
////
////        try {
////            long time = sdf.parse(date).getTime();
////            long now = System.currentTimeMillis();
////            CharSequence ago =
////                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
////            return ago+"";
////        } catch (ParseException e) {
////            e.printStackTrace();
////
////            return "";
////        }
//
//    }

    @Override
    public int getItemCount() {
        return posts.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        ImageView post_image,options;
        ImageView like,liked,comment,share,save,saved;
        TextView username,date,likes_count,comments_count;
        TextView memer,description;
        ProgressDialog pd;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            profile=itemView.findViewById(R.id.profile_image);
            post_image=itemView.findViewById(R.id.post_image);
            options=itemView.findViewById(R.id.options);
            like=itemView.findViewById(R.id.like);
            liked=itemView.findViewById(R.id.liked);
            comment=itemView.findViewById(R.id.comments);
            share=itemView.findViewById(R.id.share);
            save=itemView.findViewById(R.id.save);
            saved=itemView.findViewById(R.id.saved);
            username=itemView.findViewById(R.id.username);
            date=itemView.findViewById(R.id.date);
            likes_count=itemView.findViewById(R.id.likes_count);
            comments_count=itemView.findViewById(R.id.comments_count);
            memer=itemView.findViewById(R.id.memer);
            description=itemView.findViewById(R.id.description);
            pd=new ProgressDialog(context);



        }
    }

    private void isLiked(final String postid,final ImageView imageView) {
        Call<List<LikeModel>> call = interfaceAPI.isLike(postid);
        call.enqueue(new Callback<List<LikeModel>>() {
            @Override
            public void onResponse(Call<List<LikeModel>> call, Response<List<LikeModel>> response) {
                if(response.isSuccessful())
                {
//                    Log.d("isLike:", response.body().toString());
//                    Log.d("profileid:", profileid);
                for(LikeModel likeModel : response.body())
                    if (likeModel.equals(profileid)) {
                        imageView.setImageResource(R.drawable.ic_liked);
                        imageView.setTag("Liked");

                    }else
                    {
                        imageView.setImageResource(R.drawable.ic_like);
                        imageView.setTag("Like");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LikeModel>> call, Throwable t) {
                Log.e("Error at isLike ", t.getMessage());
            }
        });

    }

//    private void isLiked(final String postid, final ImageView imageView) {
//        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes")
//                .child(postid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(firebaseUser.getUid()).exists())
//                {
//                    imageView.setImageResource(R.drawable.ic_liked);
//                    imageView.setTag("Liked");
//                }else
//                {
//                    imageView.setImageResource(R.drawable.ic_like);
//                    imageView.setTag("Like");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void addNotifications(String userid,String postid)
    {


        Call<String> call = interfaceAPI.addNotifi(userid,profileid,"liked your post",postid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Success","OK");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
//        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid);
//
//        HashMap<String,Object> map=new HashMap<>();
//
//        map.put("userid",user.getUid());
//        map.put("comment","liked your post");
//        map.put("postid",postid);
//        map.put("ispost",true);
//
//        reference.push().setValue(map);
    }
    private void getLikesCount(final TextView like,final String postid)
    {
        Call<List<LikeModel>> call = interfaceAPI.isLike(postid);
        call.enqueue(new Callback<List<LikeModel>>() {
            @Override
            public void onResponse(Call<List<LikeModel>> call, Response<List<LikeModel>> response) {
                if(response.isSuccessful())
                {
                    like.setText(response.body().size()+"");
                }
            }

            @Override
            public void onFailure(Call<List<LikeModel>> call, Throwable t) {

            }
        });
    }
//    private void getLikesCount(final TextView like,final String postid)
//    {
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes")
//                .child(postid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                like.setText(snapshot.getChildrenCount()+"");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
    private void getCommentsCount(final TextView c,final String postid)
    {
            Call<String> call = interfaceAPI.commentCount(postid);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful())
                    {
                        c.setText(response.body()+"");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });


//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments")
//                .child(postid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                c.setText(snapshot.getChildrenCount()+"");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void isSaved(final String postid,final ImageView imageView)
    {

        Call<List<Posts>> call = interfaceAPI.getFavourite(profileid);
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        imageView.setImageResource(R.drawable.ic_save);
                        imageView.setTag("save");
                    } else {
                        for (Posts posts : response.body()) {
                            if (posts.getPostid().equals(postid)) {
                                Log.d("postid", posts.getPostid());
                                imageView.setImageResource(R.drawable.ic_saved);
                                imageView.setTag("saved");
                            } else {
                                Log.d("save", "OK");
                                imageView.setImageResource(R.drawable.ic_save);
                                imageView.setTag("save");
                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("isSave", t.getMessage());

            }
        });




//        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Favourites")
//                .child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(postid).exists())
//                {
//                    imageView.setImageResource(R.drawable.ic_saved);
//                    imageView.setTag("saved");
//                }else
//                {
//                    imageView.setImageResource(R.drawable.ic_save);
//                    imageView.setTag("save");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public void shareItem(final String url, final ProgressDialog pd) {
        pd.setTitle("Downloading..");
        pd.setMessage("Please wait");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                context.startActivity(Intent.createChooser(i, "Share Image"));
                pd.dismiss();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                pd.dismiss();
                Toast.makeText(context, "Error"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                pd.dismiss();
            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Memer" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    private void publsh(final CircleImageView profile,final TextView username,final TextView memer,final String userid )
    {
        Call<Data> call = interfaceAPI.getUser(userid);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if(response.isSuccessful()) {
                    username.setText(response.body().getUsername());
                    memer.setText(response.body().getMemer());
                    Picasso.get().load(response.body().getProfileUrl()).into(profile);
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

//    private void publsh(final CircleImageView profile,final TextView username,final TextView memer,final String userid )
//    {
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Data data=snapshot.getValue(Data.class);
//
//                username.setText(data.getUsername());
//                memer.setText(data.getMemer());
//                Picasso.get().load(data.getProfileUrl()).into(profile);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}
