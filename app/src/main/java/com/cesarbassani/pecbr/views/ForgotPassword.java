package com.cesarbassani.pecbr.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cesarbassani.pecbr.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.cesarbassani.pecbr.utils.Tools.hideSoftKeyboard;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText input_email;
    private Button btnResetPass;
    private TextView btnBack;
    private RelativeLayout activity_forgot;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //View
        input_email = findViewById(R.id.forgot_email);
        btnResetPass = findViewById(R.id.forgot_btn_reset);
        btnBack = findViewById(R.id.forgot_btn_back);
        activity_forgot = findViewById(R.id.activity_forgot_password);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.forgot_btn_back) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.forgot_btn_reset) {
            hideSoftKeyboard(ForgotPassword.this);
            resetPassword(input_email.getText().toString());
        }
    }

    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar snackbar = Snackbar.make(activity_forgot, "We have sent password to email: " + email, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar.make(activity_forgot, "Failed to send password", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
    }
}
