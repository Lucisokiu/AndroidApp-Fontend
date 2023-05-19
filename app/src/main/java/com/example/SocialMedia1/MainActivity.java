package com.example.SocialMedia1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Model.RegisterModel;
import com.example.SocialMedia1.Retrofit.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Random;


import SocialMedia1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    TextView goToLogin;
    EditText username,memer,email,password;
    Button signUp;
    ProgressDialog progressDialog;

    Random random;


//
//    String url="https://firebasestorage.googleapis.com/v0/b/signin-function.appspot.com/o/download.jpg?alt=media&token=143d5e9d-c652-465d-b513-6ee75537ad11";
//
//    String profile="https://firebasestorage.googleapis.com/v0/b/signin-function.appspot.com/o/user.svg?alt=media&token=561ed6be-db1a-4068-8023-15904ed28796";


    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();






        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Login.class));

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n=username.getText().toString();
                String m=memer.getText().toString();
                String e=email.getText().toString();
                String p=password.getText().toString();

                if (n.isEmpty())
                {
                    username.setError("Please enter name");
                }else if (m.isEmpty())
                {
                    memer.setError("Please enter memer name");
                }else if (e.isEmpty())
                {
                    email.setError("Please enter email..");
                }else if (p.isEmpty())
                {
                    password.setError("Password cannot be empty..!");
                }else
                {
                    progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Registering....");
                    progressDialog.setMessage("Please wait..We're creating account for you a short while");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Call<RegisterModel> call = interfaceAPI.registerUser(n,m,e,p);
                    call.enqueue(new Callback<RegisterModel>() {
                        @Override
                        public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response) {
                            if(response.isSuccessful()) {
                                startActivity(new Intent(MainActivity.this, Login.class));
                                Toast.makeText(MainActivity.this, "Account created!!!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterModel> call, Throwable t) {
                            Log.e("Error " , t.getMessage());
                        }
                    });
                }







            }
        });





    }

    private void init()
    {
        goToLogin=findViewById(R.id.goToLogin);
        username=findViewById(R.id.username);
        memer=findViewById(R.id.memer);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signUp=findViewById(R.id.signUp);
        random=new Random();
    }
}
