package com.elrancho.pwi.pwi_app.api;

import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetailsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface InventoryCountDetailsApi {

    @GET("{storeId}/{departmentId}/{weekEndDate}")
    Call<InventoryCountDetailsResponse> getInventoryCountDetails(
            @Header("Authorization") String token,
            @Path("storeId") String storeId,
            @Path("departmentId") String departmentId,
            @Path("weekEndDate") String weekEndDate
    );


}
