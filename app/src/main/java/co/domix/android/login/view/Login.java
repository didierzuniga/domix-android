package co.domix.android.login.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.view.DomiciliaryScore;
import co.domix.android.domiciliary.view.OrderCatched;
import co.domix.android.home.view.Home;
import co.domix.android.login.presenter.LoginPresenter;
import co.domix.android.login.presenter.LoginPresenterImpl;
import co.domix.android.user.view.Requested;
import co.domix.android.user.view.UserScore;

public class Login extends AppCompatActivity implements LoginView {

    private TextInputEditText emailField, passwordField, emailFieldForRestore;
    private Button buttonSignin, buttonSignup, buttonRestore;
    private TextView createHere, restorePassword;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private DomixApplication app;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (DomixApplication) getApplicationContext();

        presenter = new LoginPresenterImpl(this);
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.prgBlogin);
        emailField = (TextInputEditText) findViewById(R.id.txtInpEmail);
        passwordField = (TextInputEditText) findViewById(R.id.txtInpPassword);
        buttonSignin = (Button) findViewById(R.id.btnSignin);
        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailField.getText().toString().equals("")){
                    Toast.makeText(Login.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else if (passwordField.getText().toString().equals("")){
                    Toast.makeText(Login.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    showProgressBar();
                    signin(emailField.getText().toString(), passwordField.getText().toString());
                }
            }
        });

        restorePassword = (TextView) findViewById(R.id.forgotMyPassword);
        restorePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertRestorePassword();
            }
        });

        createHere = (TextView) findViewById(R.id.txtVieCreateHere);
        createHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignup();
            }
        });
    }

    @Override
    public void enableInputs() {
        emailField.setEnabled(true);
        passwordField.setEnabled(true);
        buttonSignin.setEnabled(true);
    }

    @Override
    public void disableInputs() {
        emailField.setEnabled(false);
        passwordField.setEnabled(false);
        buttonSignin.setEnabled(false);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void signinError(String err) {
        firebaseAuth.getInstance().signOut();
        enableInputs();
        hideProgressBar();
        Toast.makeText(this, R.string.toast_error_signin, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goOrderCatched(String uid, String email, int idOrder) {
        app.uId = uid;
        app.email = email;
        app.idOrder = idOrder;
        hideProgressBar();
        Intent intent = new Intent(this, OrderCatched.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goOrderRequested(String uid, String email, int idOrder) {
        app.uId = uid;
        app.email = email;
        app.idOrder = idOrder;
        hideProgressBar();
        Intent intent = new Intent(this, Requested.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goUserScore(String uid, String email, int idOrder) {
        app.uId = uid;
        app.email = email;
        app.idOrder = idOrder;
        hideProgressBar();
        Intent intent = new Intent(this, UserScore.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goDomiciliaryScore(String uid, String email, int idOrder) {
        app.uId = uid;
        app.email = email;
        app.idOrder = idOrder;
        hideProgressBar();
        Intent intent = new Intent(this, DomiciliaryScore.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goHome(String uid, String email) {
        app.uId = uid;
        app.email = email;
        hideProgressBar();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void signin(String email, String password) {
        disableInputs();
        presenter.signin(email, password, this, firebaseAuth);
    }

    @Override
    public void responseEnterEmail() {
        Toast.makeText(Login.this, R.string.toast_please_enter_email, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissDialogRestore() {
        alertDialog.dismiss();
        showProgressBar();
    }

    @Override
    public void resetPasswordSent() {
        firebaseAuth.getInstance().signOut();
        hideProgressBar();
        Toast.makeText(this, R.string.toast_restored_password, Toast.LENGTH_LONG).show();
    }

    public void goSignup() {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }

    public void alertRestorePassword() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_reset_password, null);
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        emailFieldForRestore = (TextInputEditText) view.findViewById(R.id.txtInpEmailForReset);

        buttonRestore = (Button) view.findViewById(R.id.btnResetPassword);
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.restorePassword(emailFieldForRestore.getText().toString());
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    public void responseVerifyEmailFalse() {
        firebaseAuth.getInstance().signOut();
        enableInputs();
        hideProgressBar();
        Toast.makeText(this, R.string.toast_not_verify_email, Toast.LENGTH_LONG).show();
    }
}
