package com.elrancho.pwi.pwi_app_test.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemRetrofit {

    private static final String BASE_URL = "http://ec2-3-90-133-23.compute-1.amazonaws.com:8080/pwi-app-ws-dev/items/";
    private static ItemRetrofit mInstance;
    private Retrofit retrofit;

    private ItemRetrofit(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        //GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static synchronized ItemRetrofit getInstance(){
        if(mInstance==null)
            mInstance = new ItemRetrofit();
        return mInstance;
    }

    public ItemApi getItemApi(){
        return retrofit.create(ItemApi.class);
    }



}
