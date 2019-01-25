package com.elrancho.pwi.pwi_app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.ArrayMap;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.api.UserRetrofit;
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

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword, etEmail;
    private Spinner sStore;

    private View vSignupForm;
    private View vProgressBar;

    private ProgressBarVisibility progressBarVisibility;

    private boolean pwd_status = true;

    private AwesomeText pwdShowHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        vSignupForm = findViewById(R.id.signup_form);
        vProgressBar = findViewById(R.id.signup_progress);

        getSupportActionBar().setTitle("Sign Up");

        sStore = findViewById(R.id.store);
        sStore.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.Moccasin));
                etEmail.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etEmail = findViewById(R.id.email);
        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                etUsername.requestFocus();
                return false;
            }
        });

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
                userSignUp();
                return false;
            }
        });

        pwdShowHide = findViewById(R.id.signup_pwd_show_hide);

        findViewById(R.id.btn_Sign_up).setOnClickListener(this);
        findViewById(R.id.sign_in).setOnClickListener(this);

        //Populate Store spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stores_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sStore.setAdapter(adapter);

        pwdShowHide = (AwesomeText) findViewById(R.id.signup_pwd_show_hide);

        pwdShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwd_status) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    pwd_status = false;
                    pwdShowHide.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_VISIBILITY);
                    etPassword.setSelection(etPassword.length());
                } else {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    pwd_status = true;
                    pwdShowHide.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_VISIBILITY_OFF);
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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_Sign_up:

                userSignUp();
                break;

            case R.id.sign_in:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private void userSignUp() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }
        if (username.length() < 6) {
            etUsername.setError("username should be at least 6 characters");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password is too short");
            etPassword.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!email.contains("@elranchoinc.com")) {
            etEmail.setError("Not El Rancho email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            etEmail.requestFocus();
            return;
        }


        // call the store api and populate the store spinner
        Map<String, String> jsonParams = new ArrayMap<>();
        jsonParams.put("username", username);
        jsonParams.put("password", password);
        jsonParams.put("email", email);
        jsonParams.put("storeId", sStore.getSelectedItem().toString());

        RequestBody newUser = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new JSONObject(jsonParams).toString());

        progressBarVisibility = new ProgressBarVisibility(this, vSignupForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        Call<ResponseBody> call = UserRetrofit.getInstance().getUserApi().createUser(newUser);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                progressBarVisibility.showProgress(false);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this);
                if (response.code() == 201) {
                    alertDialogBuilder.setTitle("Check your email");
                    alertDialogBuilder.setMessage("Account has been create successfully! We've sent a verification link to "+etEmail.getText().toString()+".");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        }
                    });

                } else if (response.code() == 422) {

                    alertDialogBuilder.setTitle("Account exist");
                    alertDialogBuilder.setMessage("Account " + etUsername.getText().toString()+" already exist.");
                    alertDialogBuilder.setPositiveButton("OK", null);
                }else if (response.code() == 302) {

                    alertDialogBuilder.setTitle("Email Found");
                    alertDialogBuilder.setMessage("Email " + etEmail.getText().toString()+" already Associated with another account.");
                    alertDialogBuilder.setPositiveButton("OK", null);
                } else {
                    alertDialogBuilder.setTitle("The Service is down");
                    alertDialogBuilder.setMessage("The Service is down. Please try again later");
                    alertDialogBuilder.setPositiveButton("OK", null);
                }


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                progressBarVisibility.showProgress(false);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this);
                alertDialogBuilder.setTitle("The Service is down");
                alertDialogBuilder.setMessage("The Service is down. Please try again later");
                alertDialogBuilder.setPositiveButton("OK", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }
}
