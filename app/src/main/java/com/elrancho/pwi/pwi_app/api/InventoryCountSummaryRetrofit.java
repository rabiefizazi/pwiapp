package com.elrancho.pwi.pwi_app.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InventoryCountSummaryRetrofit {

    private static final String BASE_URL = "http://ec2-34-204-11-242.compute-1.amazonaws.com:8080/pwi-app-ws/inventorycounts/";
    private static InventoryCountSummaryRetrofit mInstance;
    private Retrofit retrofit;

    private InventoryCountSummaryRetrofit(){

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

    public static synchronized InventoryCountSummaryRetrofit getInstance(){
        if(mInstance==null)
            mInstance = new InventoryCountSummaryRetrofit();
        return mInstance;
    }

    public InventoryCountSummaryApi getInventoryCountSummaryApi(){
        return retrofit.create(InventoryCountSummaryApi.class);
    }

}
