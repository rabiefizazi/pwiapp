package com.elrancho.pwi.pwi_app.api;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRetrofit {

    private static final String BASE_URL = "http://ec2-54-163-206-204.compute-1.amazonaws.com:8080/pwi-app-ws/users/";
    private static UserRetrofit mInstance;
    private Retrofit retrofit;

    private UserRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
    }

    public static synchronized UserRetrofit getInstance() {


        if (mInstance == null) {
            mInstance = new UserRetrofit();
        }

        return mInstance;
    }

    public UserApi getUserApi() {

        return retrofit.create(UserApi.class);
    }


}
