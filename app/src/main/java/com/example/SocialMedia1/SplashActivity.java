package com.example.SocialMedia1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.SocialMedia1.Model.Data;

import com.google.gson.Gson;

import SocialMedia1.R;

public class SplashActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferences=getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String profileUser =preferences.getString("profileUser","");
        Gson gson = new Gson();
        Data profile = gson.fromJson(profileUser, Data.class);

        new Handler(  ).postDelayed (new Runnable () {
            @Override
            public void run() {
                if (profile !=null)
                {
                    startActivity ( new Intent( SplashActivity.this,HomeActivity.class ) );
                }
                else
                {
                    startActivity ( new Intent ( SplashActivity.this,Login.class ) );
                }
                finish ();
            }
        },1500 );
    }
}