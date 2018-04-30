package co.domix.android.login.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.login.presenter.SignupPresenter;
import co.domix.android.login.presenter.SignupPresenterImpl;

public class Signup extends AppCompatActivity implements SignupView {

    private TextInputEditText emailFieldForSignup, passwordFieldForSignup, confirmPasswordFieldForSignup;
    private Button buttonSignup, buttonBack;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private SignupPresenter presenter;
    private DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        presenter = new SignupPresenterImpl(this);
        firebaseAuth = FirebaseAuth.getInstance();
        app = (DomixApplication) getApplicationContext();

        shaPref = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = shaPref.edit();

        progressBar = (ProgressBar) findViewById(R.id.prgBarSignup);
        emailFieldForSignup = (TextInputEditText) findViewById(R.id.txtInpEmailSignup);
        passwordFieldForSignup = (TextInputEditText) findViewById(R.id.txtInpPasswordSignup);
        confirmPasswordFieldForSignup = (TextInputEditText) findViewById(R.id.txtInpConfirmPasswordSignup);
        buttonSignup = (Button) findViewById(R.id.btnSignup);
        buttonBack = (Button) findViewById(R.id.btnBack);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogShowTerms(emailFieldForSignup.getText().toString(),
                        passwordFieldForSignup.getText().toString(),
                        confirmPasswordFieldForSignup.getText().toString());
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void dialogShowTerms(String email, String password, String confirmPassword){
        if (email.equals("") || password.equals("") || confirmPassword.equals("")){
            Toast.makeText(this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
        } else {
            int result = app.matchPassword(password, confirmPassword);
            if (result == 0){
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.text_terms_and_conditions))
                        .setPositiveButton(getString(R.string.text_agree), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signup(emailFieldForSignup.getText().toString(),
                                        passwordFieldForSignup.getText().toString(),
                                        confirmPasswordFieldForSignup.getText().toString());
                            }
                        })
                        .setNegativeButton(getString(R.string.text_do_not_agree), null)
                        .setMessage(getString(R.string.text_terms))
                        .show();
            } else if (result == 1){
                Toast.makeText(this, getString(R.string.toast_unmatch_password), Toast.LENGTH_SHORT).show();
            } else if (result == 2){
                Toast.makeText(this, getString(R.string.toast_length_password), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void signup(String email, String password, String confirmPassword) {
        showProgressBar();
        presenter.signup(email, password, confirmPassword, shaPref.getString("latitude", ""),
                        shaPref.getString("longitude", ""), this, firebaseAuth);
    }

    @Override
    public void responseErrorSignup() {
        hideProgressBar();
        Toast.makeText(this, R.string.toast_account_not_created, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void responseSuccessSignup() {
        firebaseAuth.getInstance().signOut();
        hideProgressBar();
        Toast.makeText(this, R.string.toast_account_created, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
