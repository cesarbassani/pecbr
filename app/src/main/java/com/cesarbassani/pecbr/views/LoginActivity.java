package com.cesarbassani.pecbr.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.cesarbassani.pecbr.utils.Tools.hideSoftKeyboard;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText input_email, input_password;
    TextView btnSignup, btnForgotPass;

    RelativeLayout activity_main;

    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //view
        btnLogin = findViewById(R.id.login_btn_login);
        input_email = findViewById(R.id.login_email);
        input_password = findViewById(R.id.login_password);
        btnSignup = findViewById(R.id.login_btn_signup);
        btnForgotPass = findViewById(R.id.login_btn_forgot_password);
        activity_main = findViewById(R.id.activity_main);
        progressBar = findViewById(R.id.progressBar);

        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

        //check already session, if ok-> Dashboard
        if (auth.getCurrentUser() != null)
            startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.login_btn_forgot_password) {
            startActivity(new Intent(this, ForgotPassword.class));
            finish();
        } else if (id == R.id.login_btn_signup) {
            startActivity(new Intent(this, SignUp.class));
            finish();
        } else if (id == R.id.login_btn_login) {
            hideSoftKeyboard(LoginActivity.this);
            loginUser(input_email.getText().toString(), input_password.getText().toString());
        }
    }

    private void loginUser(String email, final String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Snackbar snackbar = Snackbar.make(activity_main, "Erro de autenticação. Verifique seu email e senha!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        }
                    });
        } else {
            Snackbar snackbar = Snackbar.make(activity_main, "Informe email e senha para fazer o login!", Snackbar.LENGTH_SHORT);
            snackbar.show();

            input_email.requestFocus();
            progressBar.setVisibility(View.GONE);
        }
    }


}
