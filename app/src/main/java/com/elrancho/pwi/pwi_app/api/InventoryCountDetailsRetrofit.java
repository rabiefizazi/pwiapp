package com.elrancho.pwi.pwi_app.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InventoryCountDetailsRetrofit {

    private static final String BASE_URL = "http://ec2-54-163-206-204.compute-1.amazonaws.com:8080/pwi-app-ws/inventorycounts/";
    private static InventoryCountDetailsRetrofit mInstance;
    private Retrofit retrofit;

    private InventoryCountDetailsRetrofit(){

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
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized InventoryCountDetailsRetrofit getInstance(){
        if(mInstance==null)
            mInstance = new InventoryCountDetailsRetrofit();
        return mInstance;
    }

    public InventoryCountDetailsApi getInventoryCountDetailsApi(){
        return retrofit.create(InventoryCountDetailsApi.class);
    }

}
