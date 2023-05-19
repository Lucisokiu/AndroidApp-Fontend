package com.example.SocialMedia1.Retrofit;

import static com.example.SocialMedia1.Utils.Constants.BASE_URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import SocialMedia1.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtil {
    private Retrofit retrofit;
    public Retrofit getRetrofit(){
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if(BuildConfig.DEBUG){
            okhttpBuilder.addInterceptor(loggingInterceptor);
        }

        if(retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okhttpBuilder.build())
                    .build();
        }
        return retrofit;
    }

}
