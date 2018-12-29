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
import com.google.firebase.auth.FirebaseUser;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {

    private TextView txtWelcome;
    private EditText input_new_password;
    private Button  btnChangePass, btnLogout;
    private RelativeLayout activity_dashboard;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        //View
        txtWelcome = findViewById(R.id.dashboard_welcome);
        input_new_password = findViewById(R.id.dashboard_new_password);
        btnChangePass = findViewById(R.id.dashboard_btn_change_pass);
        btnLogout = findViewById(R.id.dashboard_btn_logout);
        activity_dashboard = findViewById(R.id.activity_dash_board);

        btnChangePass.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

        //Session check
        if (auth.getCurrentUser() != null)
            txtWelcome.setText("Bem-vindo, " + auth.getCurrentUser().getDisplayName() + " \n " + auth.getCurrentUser().getEmail() + "\n Informe seu email e clique em Enviar para\n" +
                    "solicitar uma nova senha");

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.dashboard_btn_change_pass) {
            changePassword(input_new_password.getText().toString());
        } else if (id == R.id.dashboard_btn_logout) {
            logoutUser();
        }
    }

    private void logoutUser() {
        auth.signOut();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(activity_dashboard, "Password changed", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }
}
