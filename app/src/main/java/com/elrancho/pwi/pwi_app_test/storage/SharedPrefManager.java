package com.elrancho.pwi.pwi_app_test.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.elrancho.pwi.pwi_app_test.models.responses.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "my_shared_pref";

    private static SharedPrefManager mInstance;
    private Context mContex;

    private SharedPrefManager(Context mContext){
        this.mContex=mContext;
    }

    public static synchronized SharedPrefManager getInstance(Context mContext){
        if(mInstance==null){
            mInstance = new SharedPrefManager(mContext);
        }

        return mInstance;
    }

    public void saveUser(User user){
        SharedPreferences sharedPreferences = mContex.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("token", user.getToken());
        editor.putString("userid", user.getUserid());
        editor.putString("username", user.getUsername());
        editor.putString("storeId", user.getStoreId());
        editor.apply();

    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContex.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String s = sharedPreferences.getString("userid", "");
        return sharedPreferences.getString("userid", "")!="";

    }

    public User getUuser(){
        SharedPreferences sharedPreferences = mContex.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString("token", null),
                sharedPreferences.getString("userid", null),
                sharedPreferences.getString("username", null),
                sharedPreferences.getString("storeId", null));

    }

    public void clear(){
        SharedPreferences sharedPreferences = mContex.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
