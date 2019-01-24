package com.elrancho.pwi.pwi_app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.shared.Utils;

import java.text.ParseException;

public class FrontActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_activity);

        try {
            long s = Utils.getInstance().getCurrentTimeInMilliseconds();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FrontActivity.this, LoginActivity.class));
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 1000);

    }


}
