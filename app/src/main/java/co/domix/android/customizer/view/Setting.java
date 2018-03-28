package co.domix.android.customizer.view;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.domix.android.R;
import co.domix.android.customizer.presenter.SettingPresenter;
import co.domix.android.customizer.presenter.SettingPresenterImpl;

public class Setting extends AppCompatActivity implements SettingView {

    private Switch aSwitch;
    private EditText editTextEmailReauthenticate, editTextPasswordReauthenticate, editTextPassword1, editTextPassword2;
    private Button buttonChangePassword, buttonDeleteAccount, buttonReauthenticate;
    private android.app.AlertDialog alertDialog;
    private SharedPreferences location;
    private SharedPreferences.Editor editor;
    private SettingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_user_setting);

        presenter = new SettingPresenterImpl(this);
        location = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = location.edit();

        buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = (Button) findViewById(R.id.buttonDeleteAccount);
        aSwitch = (Switch) findViewById(R.id.switchNotifications);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("notifications", true);
                    editor.commit();
                } else {
                    editor.putBoolean("notifications", false);
                    editor.commit();
                }
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReauthenticate(1);
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReauthenticate(2);
            }
        });
    }

    public void dialogReauthenticate(final int opt) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_reauthenticate, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editTextEmailReauthenticate = (EditText) view.findViewById(R.id.editTextEmalReauthenticate);
        editTextPasswordReauthenticate = (EditText) view.findViewById(R.id.editTextPasswordReauthenticate);
        buttonReauthenticate = (Button) view.findViewById(R.id.buttonReauthenticate);
        buttonReauthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = editTextEmailReauthenticate.getText().toString();
                String getPassword = editTextPasswordReauthenticate.getText().toString();
                if (getEmail.equals("") || getPassword.equals("")) {
                    Toast.makeText(Setting.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    presenter.dialogReauthenticate(getEmail, getPassword, opt);
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void goToChangePassword() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_change_password, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editTextPassword1 = (EditText) view.findViewById(R.id.editTextPassword1);
        editTextPassword2 = (EditText) view.findViewById(R.id.editTextPassword2);
        buttonChangePassword = (Button) view.findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getPassword1 = editTextPassword1.getText().toString();
                String getPassword2 = editTextPassword2.getText().toString();
                if (getPassword1.equals("") || getPassword2.equals("")) {
                    Toast.makeText(Setting.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    presenter.changePassword(getPassword1);
                    alertDialog.dismiss();
                }
            }
        });
    }
}
