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
import android.widget.Button;
import android.widget.TextView;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.adapters.InventoyCountSummaryAdapter;
import com.elrancho.pwi.pwi_app.api.InventoryCountSummaryRetrofit;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummaryResponse;
import com.elrancho.pwi.pwi_app.shared.ProgressBarVisibility;
import com.elrancho.pwi.pwi_app.shared.Utils;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerDepartment;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerInventorySummary;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCountSummaryActivity extends AppCompatActivity /*implements View.OnClickListener*/ {


    private RecyclerView recyclerView;
    private InventoyCountSummaryAdapter inventoyCountSummaryAdapter;
    private List<InventoryCountSummary> inventoryCountSummaries;

    private ProgressBarVisibility progressBarVisibility;

    private View vInventoryCountSummaryForm;
    private View vProgressBar;

    private TextView etDepartment;

    private View fab;

    private String token, storeId, departmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_count_summary_recyclerview);

        token = SharedPrefManager.getInstance(this).getUuser().getToken();

        storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();
        departmentId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getDepartmentId();

        vInventoryCountSummaryForm = findViewById(R.id.content_layout);
        vProgressBar = findViewById(R.id.inventory_count_summary_progress);

        int storeIdTitle = Integer.parseInt(SharedPrefManager.getInstance(this).getUuser().getStoreId())%1000;
        String last3digits = departmentId.substring(4);
        switch (last3digits) {
            case "108":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Dairy");
                break;
            case "200":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Meat");
                break;
            case "300":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Seafood");
                break;
            case "400":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Produce");
                break;
            case "500":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Bakery");
                break;
            case "501":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Cake");
                break;
            case "600":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Kitchen");
                break;
            case "700":
                getSupportActionBar().setTitle("Store "+storeIdTitle+"\\ Tortilleria");
                break;
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedWeekInfo(Integer.parseInt(SharedPrefManagerDepartment.getInstance(InventoryCountSummaryActivity.this).getDepartment().getStoreId()),
                        Integer.parseInt(SharedPrefManagerDepartment.getInstance(InventoryCountSummaryActivity.this).getDepartment().getDepartmentId()),
                        Utils.getInstance().getCurrentWeekEndDate(),
                        0.00,
                        0
                );
            }
        });

        retrofitCallInventoryCountSummary();

    }

    public void retrofitCallInventoryCountSummary() {


        recyclerView = findViewById(R.id.inventory_count_summary_recyclerview);

        Call<InventoryCountSummaryResponse> call = InventoryCountSummaryRetrofit
                .getInstance().getInventoryCountSummaryApi().getInventoryCountSummary(token, storeId, departmentId);

        progressBarVisibility = new ProgressBarVisibility(this, vInventoryCountSummaryForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        call.enqueue(new Callback<InventoryCountSummaryResponse>() {
            @Override
            public void onResponse(Call<InventoryCountSummaryResponse> call, Response<InventoryCountSummaryResponse> response) {
                progressBarVisibility.showProgress(false);

                inventoryCountSummaries = response.body().getInventoryCountSummaries();
                Collections.sort(inventoryCountSummaries, new Comparator<InventoryCountSummary>() {
                    @Override
                    public int compare(InventoryCountSummary o1, InventoryCountSummary o2) {
                        return o2.getWeekEndDate().compareTo(o1.getWeekEndDate());
                    }

                });
                inventoyCountSummaryAdapter = new InventoyCountSummaryAdapter(InventoryCountSummaryActivity.this, inventoryCountSummaries);
                recyclerView.setAdapter(inventoyCountSummaryAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountSummaryActivity.this));

                //disable fab button if the current week is already created
                if (inventoryCountSummaries.size() > 0 && Utils.getInstance().getCurrentWeekEndDate().equals(inventoryCountSummaries.get(0).getWeekEndDate()))
                    fab.setEnabled(false);
            }

            @Override
            public void onFailure(Call<InventoryCountSummaryResponse> call, Throwable t) {
                progressBarVisibility.showProgress(false);
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
            //finish();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveSelectedWeekInfo(Integer storeId, Integer departmentId, String weekEndDate, Double totalInventory, int position) {
        InventoryCountSummary inventoryCountSummary = new InventoryCountSummary(storeId, departmentId, weekEndDate, totalInventory, position);
        SharedPrefManagerInventorySummary.getInstance(this).clear();
        SharedPrefManagerInventorySummary.getInstance(this).saveInventoryCountSummary(inventoryCountSummary);

        Intent intent = new Intent(this, InventoryCountDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }
}