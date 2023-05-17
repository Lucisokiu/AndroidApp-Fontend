package com.example.SocialMedia1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Model.LoginModel;
import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import SocialMedia1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Login extends AppCompatActivity {
    Button sign;
    GoogleSignInClient googleSignInClient;
    TextView goToSignUp;

    EditText email,password;
    Button login;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseUser user;
//    private SessionManagement sessionManagement;

    private NetworkUtil networkUtil;
    private Retrofit retrofit;
    private InterfaceAPI interfaceAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        networkUtil = new NetworkUtil();
        retrofit = networkUtil.getRetrofit();
        interfaceAPI = retrofit.create(InterfaceAPI.class);



        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,MainActivity.class));

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e=email.getText().toString();
                String p=password.getText().toString();

                if (e.isEmpty())
                {
                    email.setError("Please enter valid email..");
                }else if (p.isEmpty())
                {
                    password.setError("Please enter password..");
                }else
                {
                    progressDialog=new ProgressDialog(Login.this);
                    progressDialog.setTitle("Logging");
                    progressDialog.setMessage("Please wait..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Call<LoginModel> call = interfaceAPI.loginUser(e,p);
                    call.enqueue(new Callback<LoginModel>() {
                        @Override
                        public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                            if(response.isSuccessful())
                            {
                                Intent intent=new Intent(Login.this,HomeActivity.class);
                                String uid = response.body().getUid();
//                                intent.putExtra("uid",uid);
//                                Log.d("uid user", response.body().getUid());


                                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                                Call<Data> calluser = interfaceAPI.getUser(uid);
                                calluser.enqueue(new Callback<Data>() {
                                    @Override
                                    public void onResponse(Call<Data> call, Response<Data> response) {
                                        Gson gson = new Gson();
                                        String profileString = gson.toJson(response.body()); // profile là một đối tượng cần lưu
                                        editor.putString("profileUser", profileString);
                                        editor.apply();
                                    }

                                    @Override
                                    public void onFailure(Call<Data> call, Throwable t) {
                                        Toast.makeText(Login.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                                editor.putString("profileid", uid);
                                editor.apply();
                                startActivity(intent);
                                Toast.makeText(Login.this, "Login success...", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this," login False", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginModel> call, Throwable t) {
                            Log.d("lỗi login","Lỗi : "+t.getMessage());
                            progressDialog.dismiss();
                        }
                    });


//                    auth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//
//                            if (task.isSuccessful())
//                            {
//                                startActivity(new Intent(Login.this,HomeActivity.class));
//                                Toast.makeText(Login.this, "Login success...", Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
//                            }else
//                            {
//                                Toast.makeText(Login.this, "Failed to login "+task.getException(), Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
//                            }
//                        }
//                    });
                }
            }
        });







    }




    private void init()
    {
        sign=findViewById(R.id.signIn);
        goToSignUp=findViewById(R.id.goToSignUp);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
    }
}