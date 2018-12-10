package com.elrancho.pwi.pwi_app.api;

import android.content.Context;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInventoryCountSummary {

    private static final String BASE_URL = "http://ec2-52-72-212-233.compute-1.amazonaws.com:8080/pwi-app-ws/inventorycounts/";
    private static RetrofitInventoryCountSummary mInstance;
    private Retrofit retrofit;

    private RetrofitInventoryCountSummary(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();

        //GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static synchronized RetrofitInventoryCountSummary getInstance(){
        if(mInstance==null)
            mInstance = new RetrofitInventoryCountSummary();
        return mInstance;
    }

    public InventoryCountSummaryApi getInventoryCountSummaryApi(){
        return retrofit.create(InventoryCountSummaryApi.class);
    }

}
