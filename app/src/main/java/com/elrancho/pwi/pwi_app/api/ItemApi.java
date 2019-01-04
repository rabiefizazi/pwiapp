package com.elrancho.pwi.pwi_app.api;

import com.elrancho.pwi.pwi_app.models.responses.ItemResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ItemApi {

    @GET("{storeId}/{vendorItem}")
    Call<ItemResponse> getItem(
            @Header("Authorization") String token,
            @Path("storeId") String storeId,
            @Path("vendorItem") String vendorItem
    );

    @POST("new")
    Call<ResponseBody> createItem(
            @Header("Authorization") String token,
            @Body RequestBody newUser
    );
}
