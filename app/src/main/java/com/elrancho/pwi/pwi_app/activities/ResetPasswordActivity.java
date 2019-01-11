package com.elrancho.pwi.pwi_app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.api.UserRetrofit;
import com.elrancho.pwi.pwi_app.models.responses.PasswordResetRequest;
import com.elrancho.pwi.pwi_app.models.responses.User;
import com.elrancho.pwi.pwi_app.shared.ProgressBarVisibility;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etEmail;

    private View vPasswordResetForm;
    private View vProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);

        etEmail = findViewById(R.id.email);
        findViewById(R.id.btn_reset_pwd).setOnClickListener(this);

        vPasswordResetForm = findViewById(R.id.reset_password_form);
        vProgressBar = findViewById(R.id.reset_password_progress);

        getSupportActionBar().setTitle("Password Reset");

    }

    @Override
    public void onClick(View v) {

        String email = etEmail.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            etEmail.requestFocus();
            return;
        }

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("email", email);

        RequestBody passwordResetRequest = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new JSONObject(jsonParams).toString());

        Call<PasswordResetRequest> call = UserRetrofit.getInstance().getUserApi().passwordResetRequest(passwordResetRequest);

        final ProgressBarVisibility progressBarVisibility = new ProgressBarVisibility(this, vPasswordResetForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        call.enqueue(new Callback<PasswordResetRequest>() {
            @Override
            public void onResponse(Call<PasswordResetRequest> call, Response<PasswordResetRequest> response) {
                progressBarVisibility.showProgress(false);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ResetPasswordActivity.this);


                if (response.code() == 200) {
                    if (response.body().getOperationResult().equals("ERROR")) {
                        alertDialogBuilder.setTitle("Error");
                        alertDialogBuilder.setMessage("The email is not associated with an account. Please try a different email. ");
                        alertDialogBuilder.setPositiveButton("OK", null);
                    }
                    else{
                        alertDialogBuilder.setTitle("Check your email");
                        alertDialogBuilder.setMessage("Instructions have been sent to "+etEmail.getText().toString()+".\n Check your email and follow the instructions to reset your password");
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            }
                        });
                    }
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
            @Override
            public void onFailure(Call<PasswordResetRequest> call, Throwable t) {

                progressBarVisibility.showProgress(false);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ResetPasswordActivity.this);
                alertDialogBuilder.setTitle("The service is down");
                alertDialogBuilder.setMessage("The service is down. Please try again later");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }

}
