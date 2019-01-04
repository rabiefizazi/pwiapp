package com.elrancho.pwi.pwi_app.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.elrancho.pwi.pwi_app.activities.InventoryCountDetailsActivity;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;
import com.elrancho.pwi.pwi_app.models.responses.Item;

public class SharedPrefManagerItem {

    private static final String SHARED_PREF_NAME = "Item_Shared_Pref";

    private static SharedPrefManagerItem mInstance;
    private Context mContext;

    public SharedPrefManagerItem(Context mContext) {
        this.mContext = mContext;
    }

    public static synchronized SharedPrefManagerItem getInstance(Context mContext) {

        if (mInstance == null)
            mInstance = new SharedPrefManagerItem(mContext);
        return mInstance;
    }

    public void saveItem(Item item) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("storeId", item.getStoreId());
        editor.putInt("itemUpc", item.getItemUPC());
        editor.putInt("vendorItem", item.getVendorItem());
        editor.putString("itemDesctription", item.getDescription());
        editor.putLong("cost", Double.doubleToRawLongBits(item.getCost()));
        editor.putString("unitOfMeasure", item.getUnitOfMeasure());

        editor.apply();

    }

    public Item getItem() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new Item(

                sharedPreferences.getInt("itemUpc", -1),
                sharedPreferences.getInt("vendorItem", -1),
                sharedPreferences.getInt("storeId", -1),
                sharedPreferences.getString("itemDesctription", null),
                null,
                Double.longBitsToDouble(sharedPreferences.getLong("cost", -1)),
                sharedPreferences.getString("unitOfMeasure", null),
                sharedPreferences.getBoolean("itemMaster", false),
                null

        );
    }

    public void clear(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isEmpty(){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return SharedPrefManagerItem.getInstance(mContext).getItem().getDescription() == null;
    }
}
