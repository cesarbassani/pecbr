package com.cesarbassani.pecbr.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;

import com.cesarbassani.pecbr.helper.Base64Custom;
import com.cesarbassani.pecbr.helper.UsuarioFirebase;
import com.cesarbassani.pecbr.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.cesarbassani.pecbr.utils.Tools.hideSoftKeyboard;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    Button btnSignUp;
    TextView btnLogin, btnForgotPass;
    EditText input_nome, input_email, input_pass;
    RelativeLayout activity_sign_up;

    private FirebaseAuth auth;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //View
        btnSignUp = findViewById(R.id.signup_btn_register);
        btnLogin = findViewById(R.id.signup_btn_login);
        btnForgotPass = findViewById(R.id.signup_btn_forgot_pass);
        input_email = findViewById(R.id.signup_email);
        input_nome = findViewById(R.id.signup_nome);
        input_pass = findViewById(R.id.signup_password);
        activity_sign_up = findViewById(R.id.activity_sign_up);

        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

        //check already session, if ok-> Dashboard
        if (auth.getCurrentUser() != null)
            startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.signup_btn_login) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.signup_btn_forgot_pass) {
            startActivity(new Intent(this, ForgotPassword.class));
            finish();
        } else if (id == R.id.signup_btn_register) {
            hideSoftKeyboard(SignUp.this);
            validarCadastroUsuario();
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void validarCadastroUsuario() {
        //Recuperar textos dos campos
        String textoNome = input_nome.getText().toString();
        String textoEmail = input_email.getText().toString();
        String textoSenha = input_pass.getText().toString();

        if (!textoNome.trim().isEmpty()) {
            if (!textoEmail.trim().isEmpty()) {
                if (!textoSenha.trim().isEmpty()) {

                    Usuario usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    signUpUser(usuario);
                } else {
                    input_pass.requestFocus();
                    snackbar = Snackbar.make(activity_sign_up, "Preencha a senha!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            } else {
                input_email.requestFocus();
                snackbar = Snackbar.make(activity_sign_up, "Preencha o email!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } else {
            input_nome.requestFocus();
            snackbar = Snackbar.make(activity_sign_up, "Preencha o nome!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void signUpUser(final Usuario usuario) {
        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String excecao = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                excecao = "Digite uma senha mais forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                excecao = "Por favor, digite um e-mail válido";
                            } catch (FirebaseAuthUserCollisionException e) {
                                excecao = "Esta conta já foi cadastrada";
                            } catch (Exception e) {
                                excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }
                            snackbar = Snackbar.make(activity_sign_up, excecao, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                            snackbar = Snackbar.make(activity_sign_up, "Usuário cadastrado com sucesso! ", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                            try {
                                String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                                usuario.setId(identificadorUsuario);
                                usuario.salvar();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
}
