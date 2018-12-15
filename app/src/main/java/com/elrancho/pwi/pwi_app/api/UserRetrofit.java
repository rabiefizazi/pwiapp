package com.elrancho.pwi.pwi_app.api;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRetrofit {

    private static final String BASE_URL = "http://ec2-52-72-212-233.compute-1.amazonaws.com:8080/pwi-app-ws/users/";
    private static UserRetrofit mInstance;
    private Retrofit retrofit;

    private UserRetrofit() {

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
