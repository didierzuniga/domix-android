package co.domix.android.login.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.R;
import co.domix.android.login.presenter.LoginPresenter;

public class Signup extends AppCompatActivity implements SignupView {

    private EditText emailFieldForSignup, passwordFieldForSignup, confirmPasswordFieldForSignup;
    private Button buttonSignup;
    private ProgressBar progressBarLogin;
    private CheckBox checkBox;
    private FirebaseAuth firebaseAuth;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        checkBox = (CheckBox) findViewById(R.id.checkTerms);
        emailFieldForSignup = (EditText) findViewById(R.id.emailSignup);
        passwordFieldForSignup = (EditText) findViewById(R.id.passwordSignup);
        confirmPasswordFieldForSignup = (EditText) findViewById(R.id.confirmPasswordSignup);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
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
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showProgressBar();
                signup(emailFieldForSignup.getText().toString(),
                        passwordFieldForSignup.getText().toString(),
                        confirmPasswordFieldForSignup.getText().toString());
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

    @Override
    public void signup(String email, String password, String confirmPassword) {
        presenter.signup(email, password, confirmPassword, this, firebaseAuth);
    }
}
