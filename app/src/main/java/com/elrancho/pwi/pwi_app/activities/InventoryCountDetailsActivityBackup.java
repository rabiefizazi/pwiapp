package com.elrancho.pwi.pwi_app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.adapters.InventoyCountDetailsAdapter;
import com.elrancho.pwi.pwi_app.api.InventoryCountDetailsRetrofit;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetails;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetailsResponse;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerDepartment;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerInventorySummary;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCountDetailsActivityBackup extends Activity {

    private RecyclerView recyclerView;
    private InventoyCountDetailsAdapter inventoyCountDetailsAdapter;
    private List<InventoryCountDetails> inventoryCounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_count_details_recyclerview);

        retrofitCallInventoryCountDetails();
    }

    public void retrofitCallInventoryCountDetails(){

        String token = SharedPrefManager.getInstance(this).getUuser().getToken();
        String storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();
        String departmentId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getDepartmentId();
        String weekEndDate = SharedPrefManagerInventorySummary.getInstance(this).getInventorySummary().getWeekEndDate();

        recyclerView = findViewById(R.id.inventory_details_count_recyclerview);

        Call<InventoryCountDetailsResponse> call = InventoryCountDetailsRetrofit
                .getInstance().getInventoryCountDetailsApi().getInventoryCountDetails(token, storeId, departmentId, weekEndDate);


        call.enqueue(new Callback<InventoryCountDetailsResponse>() {
            @Override
            public void onResponse(Call<InventoryCountDetailsResponse> call, Response<InventoryCountDetailsResponse> response) {

                inventoryCounts = response.body().getInventoryCounts();
                inventoyCountDetailsAdapter = new InventoyCountDetailsAdapter(InventoryCountDetailsActivityBackup.this, inventoryCounts);
                recyclerView.setAdapter(inventoyCountDetailsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountDetailsActivityBackup.this));
            }

            @Override
            public void onFailure(Call<InventoryCountDetailsResponse> call, Throwable t) {

            }
        });
    }
}
