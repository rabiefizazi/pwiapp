package com.elrancho.pwi.pwi_app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.adapters.InventoyCountSummaryAdapter;
import com.elrancho.pwi.pwi_app.api.RetrofitInventoryCountSummary;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummaryResponse;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerDepartment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCountSummaryActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private InventoyCountSummaryAdapter inventoyCountSummaryAdapter;
    private List<InventoryCountSummary> inventoryCountSummaries;

    private TextView test1, test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ics_fragment);

        String token = SharedPrefManager.getInstance(this).getUuser().getToken();
        String storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();
        String departmentId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getDepartmentId();

        Toast.makeText(this, storeId + " " + departmentId, Toast.LENGTH_LONG).show();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        test1 = findViewById(R.id.test1);
        test2 = findViewById(R.id.test2);


        Call<InventoryCountSummaryResponse> call = RetrofitInventoryCountSummary
                .getInstance().getInventoryCountSummaryApi().getInventoryCountSummary(token, storeId, departmentId);

        call.enqueue(new Callback<InventoryCountSummaryResponse>() {
            @Override
            public void onResponse(Call<InventoryCountSummaryResponse> call, Response<InventoryCountSummaryResponse> response) {

                inventoryCountSummaries = response.body().getInventoryCountSummaries();
                inventoyCountSummaryAdapter = new InventoyCountSummaryAdapter(InventoryCountSummaryActivity.this, inventoryCountSummaries);
                recyclerView.setAdapter(inventoyCountSummaryAdapter);
//                InventoryCountSummary ics = new InventoryCountSummary(store, dept, wed, ttl);
//                inventoryCountSummaries.add(ics);
//                inventoyCountSummaryAdapter = new InventoyCountSummaryAdapter(InventoryCountSummaryActivity.this, inventoryCountSummaries);
//                recyclerView.setAdapter(inventoyCountSummaryAdapter);


            }

            @Override
            public void onFailure(Call<InventoryCountSummaryResponse> call, Throwable t) {

            }
        });


    }
}
