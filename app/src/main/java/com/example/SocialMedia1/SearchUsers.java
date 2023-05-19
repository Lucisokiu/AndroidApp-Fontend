package com.example.SocialMedia1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.SocialMedia1.API.InterfaceAPI;
import com.example.SocialMedia1.Adapter.SearchAdapter;
import com.example.SocialMedia1.Model.Data;
import com.example.SocialMedia1.Retrofit.NetworkUtil;


import java.util.ArrayList;
import java.util.List;

import SocialMedia1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchUsers extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Data> dataList;
    SearchAdapter adapter;
    Toolbar toolbar;

    EditText et_search;



    NetworkUtil networkUtil = new NetworkUtil();
    Retrofit retrofit = networkUtil.getRetrofit();
    InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        et_search=findViewById(R.id.et_search);


        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        readUsers();

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dataList=new ArrayList<>();

        adapter=new SearchAdapter(this,dataList);
        recyclerView.setAdapter ( adapter );


    }
    private void readUsers() {
        Call<List<Data>> callData = interfaceAPI.getAllUsers();
        callData.enqueue(new Callback<List<Data>>() {
            @Override
            public void onResponse(Call<List<Data>> call, Response<List<Data>> response) {
                if(et_search.getText().toString().equals(""))
                {
                    dataList.clear();
                    for(Data data : response.body())
                    {
                        dataList.add(data);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Data>> call, Throwable t) {
                Toast.makeText(SearchUsers.this, "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
private void searchUsers(String a)
{
    Call<List<Data>> callData = interfaceAPI.getAllUsers();
    callData.enqueue(new Callback<List<Data>>() {
        @Override
        public void onResponse(Call<List<Data>> call, Response<List<Data>> response) {
            dataList.clear ();
            for(Data user : response.body())
                {
                    if (a.equals(user.getMemer())) {
                        dataList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        @Override
        public void onFailure(Call<List<Data>> call, Throwable t) {
            Toast.makeText(SearchUsers.this, "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SearchUsers.this,HomeActivity.class));
    }
}
