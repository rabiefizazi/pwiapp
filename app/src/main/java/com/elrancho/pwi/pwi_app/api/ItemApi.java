package com.elrancho.pwi.pwi_app.api;

import com.elrancho.pwi.pwi_app.models.responses.ItemResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ItemApi {

    @GET("{storeId}/{vendorItem}")
    Call<ItemResponse> getItem(
            @Header("Authorization") String token,
            @Path("storeId") String storeId,
            @Path("vendorItem") String vendorItem
    );
}
