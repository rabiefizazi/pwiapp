package com.elrancho.pwi.pwi_app.api;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUser {

    private static final String BASE_URL = "http://ec2-34-204-11-242.compute-1.amazonaws.com:8080/pwi-app-ws/users/";
    private static RetrofitUser mInstance;
    private Retrofit retrofit;

    private RetrofitUser() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
    }

    public static synchronized RetrofitUser getInstance() {


        if (mInstance == null) {
            mInstance = new RetrofitUser();
        }

        return mInstance;
    }

    public UserApi getUserApi() {

        return retrofit.create(UserApi.class);
    }


}
