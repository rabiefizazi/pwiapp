package com.elrancho.pwi.pwi_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.adapters.InventoyCountSummaryAdapter;
import com.elrancho.pwi.pwi_app.api.RetrofitInventoryCountSummary;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummaryResponse;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerDepartment;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerInventorySummary;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCountSummaryActivity extends AppCompatActivity /*implements View.OnClickListener*/ {


    private RecyclerView recyclerView;
    private InventoyCountSummaryAdapter inventoyCountSummaryAdapter;
    private List<InventoryCountSummary> inventoryCountSummaries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_count_summary_recyclerview);

        retrofitCallInventoryCountSummary();
    }

    public void retrofitCallInventoryCountSummary(){

        String token = SharedPrefManager.getInstance(this).getUuser().getToken();
        String storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();
        String departmentId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getDepartmentId();

        recyclerView = findViewById(R.id.inventory_summary_count_recyclerview);

        Call<InventoryCountSummaryResponse> call = RetrofitInventoryCountSummary
                .getInstance().getInventoryCountSummaryApi().getInventoryCountSummary(token, storeId, departmentId);

        call.enqueue(new Callback<InventoryCountSummaryResponse>() {
            @Override
            public void onResponse(Call<InventoryCountSummaryResponse> call, Response<InventoryCountSummaryResponse> response) {

                inventoryCountSummaries = response.body().getInventoryCountSummaries();
                inventoyCountSummaryAdapter = new InventoyCountSummaryAdapter(InventoryCountSummaryActivity.this, inventoryCountSummaries);
                recyclerView.setAdapter(inventoyCountSummaryAdapter);

                recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountSummaryActivity.this));

                //Saving the inventoryCountSummaries in the SharedPref as a String using JSON
                ArrayList<InventoryCountSummaryResponse> icsList = new ArrayList<>();
                String icsString = new Gson().toJson(icsList);
                //SharedPrefManagerInventorySummary.getInstance(InventoryCountSummaryActivity.this).saveInventoryCountSummary(icsString);
            }

            @Override
            public void onFailure(Call<InventoryCountSummaryResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            SharedPrefManager.getInstance(this).clear();
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
