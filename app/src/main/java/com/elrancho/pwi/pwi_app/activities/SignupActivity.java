package com.elrancho.pwi.pwi_app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.ArrayMap;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.api.RetrofitUser;
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

    private boolean pwd_status = true;

    private AwesomeText pwdShowHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etEmail = findViewById(R.id.email);
        sStore = findViewById(R.id.store);

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


        Call<ResponseBody> call = RetrofitUser.getInstance().getUserApi().createUser(newUser);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 201) {

                    ResponseBody dr = response.body();
                    Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_LONG).show();

                } else if (response.code() == 422) {
                    Toast.makeText(SignupActivity.this, "User already exist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignupActivity.this, "The Service is down. Please try again later", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(SignupActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}
