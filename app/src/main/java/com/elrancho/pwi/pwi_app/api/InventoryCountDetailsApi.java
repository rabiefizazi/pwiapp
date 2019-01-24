package com.elrancho.pwi.pwi_app.api;

import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetailsResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface InventoryCountDetailsApi {

    @GET("{storeId}/{departmentId}/{weekEndDate}")
    Call<InventoryCountDetailsResponse> getInventoryCountDetails(
            @Header("Authorization") String token,
            @Path("storeId") String storeId,
            @Path("departmentId") String departmentId,
            @Path("weekEndDate") String weekEndDate
    );

    @GET("{storeId}/{departmentId}/{vendorItem}/{weekEndDate}")
    Call<InventoryCountDetailsResponse> getInventoryCountDetail(
            @Header("Authorization") String token,
            @Path("storeId") String storeId,
            @Path("departmentId") String departmentId,
            @Path("vendorItem") String vendorItem,
            @Path("weekEndDate") String weekEndDate
    );

    @POST("new")
    Call<ResponseBody> createInventoryCountDetail(
            @Header("Authorization") String token,
            @Body RequestBody newInventoryCount
    );

    @PUT("update")
    Call<ResponseBody> updateInventoryCountDetail(
            @Header("Authorization") String token,
            @Body RequestBody newInventoryCount
    );

}
