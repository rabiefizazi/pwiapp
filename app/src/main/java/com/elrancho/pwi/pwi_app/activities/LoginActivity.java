package com.elrancho.pwi.pwi_app.activities;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.api.RetrofitUser;
import com.elrancho.pwi.pwi_app.models.responses.User;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cyd.awesome.material.AwesomeText;
import cyd.awesome.material.FontCharacterMaps;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private AwesomeText passwordShowHide;

    private boolean pwd_status = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        findViewById(R.id.btn_Sign_in).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);

        passwordShowHide = (AwesomeText) findViewById(R.id.pwd_show_hide);
        passwordShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwd_status) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    pwd_status = false;
                    passwordShowHide.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_VISIBILITY);
                    etPassword.setSelection(etPassword.length());
                } else {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    pwd_status = true;
                    passwordShowHide.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_VISIBILITY_OFF);
                    etPassword.setSelection(etPassword.length());

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            Intent intent = new Intent(this, DepartmentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_Sign_in:
                userLogin();
                break;
            case R.id.register:
                startActivity(new Intent(this, SignupActivity.class));
        }
    }

    public void userLogin() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }
        if (username.length() < 6) {
            etUsername.setError("username should be at least 6 characters long");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters long");
            etPassword.requestFocus();
            return;
        }

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("username", username);
        jsonParams.put("password", password);

        RequestBody userLogin = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new JSONObject(jsonParams).toString());

        Call<ResponseBody> call = RetrofitUser.getInstance().getUserApi().userLogin(userLogin);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    String token = response.headers().get("Authorization");
//                    token = token.replace("Bearer ", "");
                    String userId = response.headers().get("userid");
                    String username = response.headers().get("Username");
                    String storeId = response.headers().get("storeId");
                    User loggedUser = new User(token, userId, username, storeId);

                    SharedPrefManager.getInstance(LoginActivity.this).saveUser(loggedUser);

                    Intent intent = new Intent(LoginActivity.this, DepartmentActivity.class);
                    String t = SharedPrefManager.getInstance(LoginActivity.this).getUuser().getToken();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (response.code() == 401) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setTitle("Login failed");
                    alertDialogBuilder.setMessage("Your password and user ID do not match. Please try again or reset your password");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(LoginActivity.this, "The service is down. Please try again later", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}

