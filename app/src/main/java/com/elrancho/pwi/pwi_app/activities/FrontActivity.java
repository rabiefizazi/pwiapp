package com.elrancho.pwi.pwi_app.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.activities.LoginActivity;

public class FrontActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FrontActivity.this, LoginActivity.class));
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 2000);

    }


}
