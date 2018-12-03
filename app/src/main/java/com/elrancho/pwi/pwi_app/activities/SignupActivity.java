package com.elrancho.pwi.pwi_app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.elrancho.pwi.pwi_app.R;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword, etEmail, etStore;
    private TextView tvSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etEmail = findViewById(R.id.email);
        etStore = findViewById(R.id.store);

        findViewById(R.id.btn_Sign_up).setOnClickListener(this);
        findViewById(R.id.sign_in).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_Sign_up:

                userSignUp();
                break;

            case R.id.sign_in:
                break;
        }
    }

    private void userSignUp() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String store = etStore.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etUsername.setError("Password is required");
            etUsername.requestFocus();
            return;
        }

        if (password.length()<6) {
            etUsername.setError("Password is too short");
            etUsername.requestFocus();
            return;
        }


        if (email.isEmpty()) {
            etUsername.setError("Email is required");
            etUsername.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUsername.setError("Invalid email");
            etUsername.requestFocus();
            return;
        }

        

    }
}
