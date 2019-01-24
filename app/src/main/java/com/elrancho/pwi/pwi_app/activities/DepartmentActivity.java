package com.elrancho.pwi.pwi_app.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.models.responses.Department;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerDepartment;

public class DepartmentActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.departments_activity);

        String token = SharedPrefManager.getInstance(this).getUuser().getToken();

        findViewById(R.id.bakery_card).setOnClickListener(this);
        findViewById(R.id.cake_card).setOnClickListener(this);
        findViewById(R.id.dairy_card).setOnClickListener(this);
        findViewById(R.id.kitchen_card).setOnClickListener(this);
        findViewById(R.id.meat_card).setOnClickListener(this);
        findViewById(R.id.produce_card).setOnClickListener(this);
        findViewById(R.id.seafood_card).setOnClickListener(this);
        findViewById(R.id.tortilleria_card).setOnClickListener(this);

        int storeIdTitle = Integer.parseInt(SharedPrefManager.getInstance(this).getUuser().getStoreId())%1000;

        getSupportActionBar().setTitle("Store "+storeIdTitle);
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {

        String storeId = SharedPrefManager.getInstance(DepartmentActivity.this).getUuser().getStoreId();

        switch (view.getId()) {
            case R.id.bakery_card:
                getInventorySummary(storeId, storeId.concat("500"));
                break;
            case R.id.cake_card:
                getInventorySummary(storeId, storeId.concat("501"));
                break;
            case R.id.dairy_card:
                getInventorySummary(storeId, storeId.concat("108"));
                break;
            case R.id.kitchen_card:
                getInventorySummary(storeId, storeId.concat("600"));
                break;
            case R.id.meat_card:
                getInventorySummary(storeId, storeId.concat("200"));
                break;
            case R.id.produce_card:
                getInventorySummary(storeId, storeId.concat("400"));
                break;
            case R.id.seafood_card:
                getInventorySummary(storeId, storeId.concat("300"));
                break;
            case R.id.tortilleria_card:
                getInventorySummary(storeId, storeId.concat("700"));
                break;
        }
    }

    public void getInventorySummary(String storeId, String departmentId) {

        Department department = new Department(storeId, departmentId);
        SharedPrefManagerDepartment.getInstance(this).clear();
        SharedPrefManagerDepartment.getInstance(this).saveDepartment(department);

        Intent intent = new Intent(DepartmentActivity.this, InventoryCountSummaryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
