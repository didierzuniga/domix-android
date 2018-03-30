package co.domix.android.login.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.R;
import co.domix.android.login.presenter.SignupPresenter;
import co.domix.android.login.presenter.SignupPresenterImpl;

public class Signup extends AppCompatActivity implements SignupView {

    private TextInputEditText emailFieldForSignup, passwordFieldForSignup, confirmPasswordFieldForSignup;
    private TextView terms;
    private Button buttonSignup, buttonBack;
    private ProgressBar progressBarLogin;
    private CheckBox checkBox;
    private FirebaseAuth firebaseAuth;
    private SignupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        presenter = new SignupPresenterImpl(this);
        firebaseAuth = FirebaseAuth.getInstance();

        checkBox = (CheckBox) findViewById(R.id.checkTerms);
        terms = (TextView) findViewById(R.id.textViewTerms);
        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarlogin);
        emailFieldForSignup = (TextInputEditText) findViewById(R.id.emailSignup);
        passwordFieldForSignup = (TextInputEditText) findViewById(R.id.passwordSignup);
        confirmPasswordFieldForSignup = (TextInputEditText) findViewById(R.id.confirmPasswordSignup);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonSignup.setEnabled(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonSignup.setEnabled(true);
                } else {
                    buttonSignup.setEnabled(false);
                }
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowTerms();
            }
        });
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showProgressBar();
                signup(emailFieldForSignup.getText().toString(),
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
        progressBarLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarLogin.setVisibility(View.GONE);
    }

    public void dialogShowTerms(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.text_terms_and_conditions))
                .setPositiveButton(getString(R.string.text_agree), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkBox.setChecked(true);
                    }
                })
                .setNegativeButton(getString(R.string.text_do_not_agree), null)
                .setMessage(getString(R.string.text_terms))
                .show();
    }

    @Override
    public void signup(String email, String password, String confirmPassword) {
        presenter.signup(email, password, confirmPassword, this, firebaseAuth);
    }

    @Override
    public void responseCompleteAllFiles() {
        hideProgressBar();
        Toast.makeText(this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void responseUnmatchPassword() {
        hideProgressBar();
        Toast.makeText(this, R.string.toast_unmatch_password, Toast.LENGTH_SHORT).show();
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
