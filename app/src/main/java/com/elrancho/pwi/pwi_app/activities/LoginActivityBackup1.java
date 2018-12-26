package com.elrancho.pwi.pwi_app.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.api.UserRetrofit;
import com.elrancho.pwi.pwi_app.models.responses.User;
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
public class LoginActivityBackup1 extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private AwesomeText passwordShowHide;

    private View vloginFromView;
    private View vProgressView;

    private boolean pwd_status = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        vloginFromView=findViewById(R.id.login_form);
        vProgressView=findViewById(R.id.login_progress);

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
                attemptLogin();
                break;
            case R.id.register:
                startActivity(new Intent(this, SignupActivity.class));
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


        //showProgress(true);
        UserLoginTask userLoginTask = new UserLoginTask(etUsername.getText().toString(), etPassword.getText().toString());
        userLoginTask.execute();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Void>{

        Call<ResponseBody> call;
        private String username;
        private String password;

        public UserLoginTask(String username, String password){
            this.username=username;
            this.password=password;
        }
        @Override
        protected Void doInBackground(Void... voids) {

            Map<String, Object> jsonParams = new ArrayMap<>();
            jsonParams.put("username", username);
            jsonParams.put("password", password);

            RequestBody userLogin = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new JSONObject(jsonParams).toString());

            call = UserRetrofit.getInstance().getUserApi().userLogin(userLogin);


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {

                        String token = response.headers().get("Authorization");
                        String userId = response.headers().get("userid");
                        String username = response.headers().get("Username");
                        String storeId = response.headers().get("storeId");
                        User loggedUser = new User(token, userId, username, storeId);

                        SharedPrefManager.getInstance(LoginActivityBackup1.this).saveUser(loggedUser);

                        Intent intent = new Intent(LoginActivityBackup1.this, DepartmentActivity.class);
                        String t = SharedPrefManager.getInstance(LoginActivityBackup1.this).getUuser().getToken();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else if (response.code() == 401) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivityBackup1.this);
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
                        Toast.makeText(LoginActivityBackup1.this, "The service is down. Please try again later", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast.makeText(LoginActivityBackup1.this, t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
            showProgress(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            showProgress(true);
        }

        //
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            showProgress(false);
//        }
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            vloginFromView.setVisibility(show ? View.GONE : View.VISIBLE);
            vloginFromView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vloginFromView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            vProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            vProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            vProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            vloginFromView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

