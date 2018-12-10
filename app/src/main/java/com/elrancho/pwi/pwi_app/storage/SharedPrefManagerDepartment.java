package com.elrancho.pwi.pwi_app.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.elrancho.pwi.pwi_app.models.responses.Department;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;

public class SharedPrefManagerDepartment {

    private static final String SHARED_PREF_NAME = "Department_Shared_Pref";

    private static SharedPrefManagerDepartment mInstance;
    private Context mContext;

    public SharedPrefManagerDepartment(Context mContext) {
        this.mContext = mContext;
    }

    public static synchronized SharedPrefManagerDepartment getInstance(Context mContext) {

        if (mInstance == null)
            mInstance = new SharedPrefManagerDepartment(mContext);
        return mInstance;
    }

    public void saveDepartment(Department department) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("storeId", department.getStoreId());
        editor.putString("departmentId", department.getDepartmentId());

        editor.apply();

    }

    public Department getDepartment() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new Department(
                sharedPreferences.getString("storeId", null),
                sharedPreferences.getString("departmentId", null)
        );
    }

    public void clear(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
