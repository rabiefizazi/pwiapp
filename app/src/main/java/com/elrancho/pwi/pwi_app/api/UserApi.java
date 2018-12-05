package com.elrancho.pwi.pwi_app.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    @POST("new")
    Call<ResponseBody> createUser(@Body RequestBody newUser);
    @POST("login")
    Call<ResponseBody> userLogin(@Body RequestBody newUser);
}
