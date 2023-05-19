package com.example.SocialMedia1.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.PhotosAdapter;
import com.example.SocialMedia1.Login;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.FollowerModel;
import com.example.SocialMedia1.Model.FollowingModel;
import com.example.SocialMedia1.Model.Posts;
import SocialMedia1.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.SocialMedia1.Model.Profile;
import com.example.SocialMedia1.RealPathUtil.RealPathUtil;
import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.example.SocialMedia1.ShowList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class UserFragment extends Fragment {

    List<String> listfollowing;
    List<String> listfollowers;


    TextView followers,following,logout;
    Button btn_update,u_bg,settings;
    ImageView profile;
    ImageView bg,bg_camera;
    TextView username,memer,following_count,followers_count,pos_count;
    ProgressDialog pd;

    RecyclerView recyclerView;
    List<Posts> postsList;
    PhotosAdapter adapter;


    String profileid;


    Uri profileUri,bgUri;
    String RealpostUriProfile, RealpostUriBackground;

    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    public UserFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user, container, false);


        SharedPreferences preferences=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=preferences.getString("profileid","");
        Log.d("profileid", profileid);
        init(view);

//        auth=FirebaseAuth.getInstance();
//        user=auth.getCurrentUser();

//        reference= FirebaseDatabase.getInstance().getReference().child("Users");
//        storageReference= FirebaseStorage.getInstance().getReference().child("Profiles");
//        bgRef= FirebaseStorage.getInstance().getReference().child("Backgrounds");


        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        listfollowing=new ArrayList<>();
        listfollowers=new ArrayList<>();

        postsList=new ArrayList<>();
        adapter=new PhotosAdapter(getContext(),postsList);


        recyclerView.setAdapter(adapter);
        getImages();


        clicks();
        getUserData();
        getFollowCount();
        listfollowing=new ArrayList<>();
        listfollowers=new ArrayList<>();
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("myList", (ArrayList<String>) listfollowers);
                Intent intent=new Intent(getActivity(), ShowList.class);
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
                Intent intent=new Intent(getActivity(), ShowList.class);
                intent.putExtra("id",profileid);
                intent.putExtra("myBundle",bundle);
                intent.putExtra("title",following.getText().toString());
                startActivity(intent);
            }
        });


        return view;
//

    }

    private void clicks()
    {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

                // Lấy đối tượng SharedPreferences.Editor
                SharedPreferences.Editor editor = preferences.edit();

                // Xóa toàn bộ dữ liệu trong SharedPreferences
                editor.clear();

                // Áp dụng các thay đổi
                editor.apply();
                startActivity(new Intent(getActivity(), Login.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission_forProfile();
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });
        bg_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CheckPermission_forBg();
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,2);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadProfile();

            }
        });

        u_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBackground();
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data !=null)
        {
            profileUri=data.getData();
            profile.setImageURI(profileUri);
            profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btn_update.setVisibility(View.VISIBLE);

        }else if (requestCode==2 && resultCode==RESULT_OK && data !=null)
        {
            bgUri=data.getData();
            bg.setImageURI(bgUri);
            bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            u_bg.setVisibility(View.VISIBLE);
        }
    }

    private void uploadProfile()
    {


            pd.setTitle("Profile Picture");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            if (profileUri !=null) {
                // Tạo request body với định dạng form data
                RealpostUriProfile = RealPathUtil.getRealPath(getContext(), profileUri);
                File imageFile = new File(RealpostUriProfile);
//                File imageFile = new File(profileUri.getPath());

                MultipartBody.Part imagePart = MultipartBody.Part.createFormData( "image",profileid, RequestBody.create(MediaType.parse(getFileExtension(profileUri)), imageFile));

                // Gửi request đến server bằng Retrofit
                Call<String> call = interfaceAPI.uploadProfile(imagePart);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Profiled updated", Toast.LENGTH_LONG).show();
                        btn_update.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        pd.dismiss();
                        btn_update.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Something went wrong!!", Toast.LENGTH_LONG).show();
                        Log.e("Error at UploadProfile ", t.getMessage());
                    }
                });
            }else {
                Toast.makeText(getContext(), "No image is selected!!", Toast.LENGTH_SHORT).show();

            }
    }

    private void updateBackground()
    {
        pd.setTitle("Background image");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        if (bgUri !=null) {
            // Tạo request body với định dạng form data
            RealpostUriBackground = RealPathUtil.getRealPath(getContext(), profileUri);
            File imageFile = new File(RealpostUriBackground);

            RequestBody requestBody = RequestBody.create(MediaType.parse(getFileExtension(profileUri)), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", profileid, requestBody);

            // Gửi request đến server bằng Retrofit
            Call<String> call = interfaceAPI.uploadProfile(imagePart);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Background updated", Toast.LENGTH_SHORT).show();
                    u_bg.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    pd.dismiss();
                    u_bg.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    Log.e("Error at updateBackground ", t.getMessage());
                }
            });
        }else {
            Toast.makeText(getContext(), "No image is selected!!", Toast.LENGTH_SHORT).show();

        }

    }

    private void getFollowCount()
    {
            Call<List<FollowingModel>> followingModelCall = interfaceAPI.getFollowing(profileid);
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


        Call<List<FollowerModel>> followersCall = interfaceAPI.getFollowers(profileid);
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
        });

    }

    private void getUserData()
    {
//        Log.d("profileid",profileid);

        Call<Data> call = interfaceAPI.getUser(profileid);
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
                Picasso.get().load(b).placeholder(R.drawable.profile_image).into(bg);


//                Glide.with(getActivity()).load(p).centerCrop().placeholder(R.drawable.profile_image).into(profile);
//                Glide.with(getActivity()).load(b).centerCrop().placeholder(R.drawable.profile_image).into(bg);

            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();

        // Truy xuất tới thực thể file tương ứng với Uri
        String type = contentResolver.getType(uri);
        if (type != null && type.indexOf('/') != -1) {
            // Nếu có kiểu mime, lấy mở rộng từ nó
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
        } else {
            // Nếu không có kiểu mime hoặc không tìm thấy dấu /, lấy mở rộng từ đường dẫn file
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            return extension.toLowerCase();
        }
    }

    private void init(View view)
    {
        settings=view.findViewById(R.id.settings);

        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        logout=view.findViewById(R.id.logout);
        profile=view.findViewById(R.id.profile_image);
        username=view.findViewById(R.id.username);
        memer=view.findViewById(R.id.memer);
        btn_update=view.findViewById(R.id.btn_update);
        u_bg=view.findViewById(R.id.update_bg);
        bg=view.findViewById(R.id.background);
        following_count=view.findViewById(R.id.following_count);
        followers_count=view.findViewById(R.id.followers_count);
        pos_count=view.findViewById(R.id.posts);
        bg_camera=view.findViewById(R.id.upload_background);
        pd=new ProgressDialog(getContext());


    }

    private void getImages()
    {
        Call<List<Posts>> call = interfaceAPI.getPost();
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                postsList.clear();
                int i = 0;
                for ( Posts posts : response.body()) {
                    if (posts.getPublisher().equals(profile)) {

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



    public static String[] storge_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE

    };
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };


    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }
    private void CheckPermission_forProfile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            requestPermissions(permissions(),100);
        }
    }
    private void CheckPermission_forBg() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            requestPermissions(permissions(),200);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }










            }


