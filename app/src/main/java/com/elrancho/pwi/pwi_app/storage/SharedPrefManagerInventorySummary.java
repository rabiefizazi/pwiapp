package com.elrancho.pwi.pwi_app.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;

public class SharedPrefManagerInventorySummary {

    private static final String SHARED_PREF_NAME = "InventoryCount_Shared_Pref";

    private static SharedPrefManagerInventorySummary mInstance;
    private Context mContext;

    public SharedPrefManagerInventorySummary(Context mContext) {
        this.mContext = mContext;
    }

    public static synchronized SharedPrefManagerInventorySummary getInstance(Context mContext) {

        if (mInstance == null)
            mInstance = new SharedPrefManagerInventorySummary(mContext);
        return mInstance;
    }

    public void saveInventoryCountSummary(InventoryCountSummary inventoryCountSummary) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("storeId", inventoryCountSummary.getStoreId());
        editor.putInt("departmentId", inventoryCountSummary.getDepartmentId());
        editor.putString("weekEndDate", inventoryCountSummary.getWeekEndDate());
        editor.putLong("totalInventory", Double.doubleToRawLongBits(inventoryCountSummary.getTotalInventory()));
        editor.putInt("position", inventoryCountSummary.getPosition());

        editor.apply();

    }

    public InventoryCountSummary getInventorySummary() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new InventoryCountSummary(
                sharedPreferences.getInt("storeId", -1),
                sharedPreferences.getInt("departmentId", -1),
                sharedPreferences.getString("weekEndDate", null),
                Double.longBitsToDouble(sharedPreferences.getLong("totalInventory", -1)),
                sharedPreferences.getInt("position", -1)
        );
    }

    public void clear(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
