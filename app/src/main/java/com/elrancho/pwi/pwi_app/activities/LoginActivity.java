package com.elrancho.pwi.pwi_app.activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.api.UserRetrofit;
import com.elrancho.pwi.pwi_app.models.responses.User;
import com.elrancho.pwi.pwi_app.shared.ProgressBarVisibility;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;

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
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private AwesomeText passwordShowHide;

    private View vLoginForm;
    private View vProgressBar;
    private boolean pwd_status = true;

    private ProgressBarVisibility progressBarVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        etUsername = findViewById(R.id.username);
        etUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                etPassword.requestFocus();
                return false;
            }
        });

        etPassword = findViewById(R.id.password);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                attemptLogin();
                return false;
            }
        });

        vLoginForm = findViewById(R.id.login_form);
        vProgressBar = findViewById(R.id.login_progress);

        findViewById(R.id.btn_Sign_in).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.reset_password).setOnClickListener(this);

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

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, DepartmentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_Sign_in:
                attemptLogin();
                break;
            case R.id.register:
                startActivity(new Intent(this, SignupActivity.class));
                break;
            case R.id.reset_password:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
        }
    }

    public void attemptLogin() {

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

        progressBarVisibility = new ProgressBarVisibility(this, vLoginForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        Call<ResponseBody> call = UserRetrofit.getInstance().getUserApi().userLogin(userLogin);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBarVisibility.showProgress(false);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setTitle("Login failed");

                if (response.code() == 200) {
                    String token = response.headers().get("Authorization");
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
                    String failedMessage = response.headers().get("failedMessage");
                    if (failedMessage.equals("Bad credentials"))
                        alertDialogBuilder.setMessage("Your password and user ID do not match. Please try again or reset your password");
                    else
                        alertDialogBuilder.setMessage("You're account has not been verified yet. Please check your email to verify your account");

                    alertDialogBuilder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    alertDialogBuilder.setMessage("The service is down. Please try again later");
                    alertDialogBuilder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBarVisibility.showProgress(false);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setTitle("Login failed");
                alertDialogBuilder.setMessage("The service is down. Please try again later");
                alertDialogBuilder.setPositiveButton("OK", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Log.d("===", t.getMessage());

            }
        });
    }
}

