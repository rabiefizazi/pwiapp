package com.elrancho.pwi.pwi_app_test.api;

import com.elrancho.pwi.pwi_app_test.models.responses.InventoryCountSummaryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface InventoryCountSummaryApi {

@GET("totalInventory/{storeId}/{departmentId}")
    Call<InventoryCountSummaryResponse> getInventoryCountSummary(
        @Header("Authorization") String token,
        @Path("storeId") String storeId,
        @Path("departmentId") String departmentId
);

}
