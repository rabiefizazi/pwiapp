package com.elrancho.pwi.pwi_app_test.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.elrancho.pwi.pwi_app_test.models.responses.Item;

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

        editor.putLong("storeId", item.getStoreId());
        editor.putLong("itemUpc", item.getItemUPC());
        editor.putLong("vendorItem", item.getVendorItem());
        editor.putString("itemDesctription", item.getDescription());
        editor.putLong("cost", Double.doubleToRawLongBits(item.getCost()));
        editor.putString("unitOfMeasure", item.getUnitOfMeasure());

        editor.apply();

    }

    public Item getItem() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new Item(

                sharedPreferences.getLong("itemUpc", -1),
                sharedPreferences.getLong("vendorItem", -1),
                sharedPreferences.getLong("storeId", -1),
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
