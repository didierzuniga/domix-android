package co.domix.android.customizer.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.presenter.SettingPresenter;
import co.domix.android.customizer.presenter.SettingPresenterImpl;
import co.domix.android.home.view.Home;
import co.domix.android.login.view.Login;
import co.domix.android.services.NotificationService;

public class Setting extends AppCompatActivity implements SettingView, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private Switch aSwitch;
    private TextInputEditText editTextEmailReauthenticate, editTextPasswordReauthenticate, editTextPassword1, editTextPassword2;
    private Button buttonChangePassword, buttonDeleteAccount, buttonReauthenticate, buttonBack;
    private android.app.AlertDialog alertDialog;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private SettingPresenter presenter;
    private DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_user_setting);

        app = (DomixApplication) getApplicationContext();
        presenter = new SettingPresenterImpl(this);
        shaPref = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = shaPref.edit();

        progressBar = (ProgressBar) findViewById(R.id.progressBarSetting);
        buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = (Button) findViewById(R.id.buttonDeleteAccount);
        aSwitch = (Switch) findViewById(R.id.switchNotifications);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("activeNotification", true);
                    editor.commit();
                    startService(new Intent(Setting.this, NotificationService.class));
                } else {
                    editor.putBoolean("activeNotification", false);
                    editor.commit();
                    stopService(new Intent(Setting.this, NotificationService.class));
                }
            }
        });

        if (shaPref.getBoolean("activeNotification", false)){
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryLight)); //Change menu hamburguer color
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void dialogReauthenticate(final int opt) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_reauthenticate, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        editTextEmailReauthenticate = (TextInputEditText) view.findViewById(R.id.editTextEmalReauthenticate);
        editTextPasswordReauthenticate = (TextInputEditText) view.findViewById(R.id.editTextPasswordReauthenticate);
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
                    showProgressBar();
                }
            }
        });
        buttonBack = (Button) view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    public void goToChangePassword() {
        hideProgressBar();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_change_password, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editTextPassword1 = (TextInputEditText) view.findViewById(R.id.editTextPassword1);
        editTextPassword2 = (TextInputEditText) view.findViewById(R.id.editTextPassword2);
        buttonChangePassword = (Button) view.findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getPassword1 = editTextPassword1.getText().toString();
                String getPassword2 = editTextPassword2.getText().toString();
                if (getPassword1.equals("") || getPassword2.equals("")) {
                    Toast.makeText(Setting.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    int result = app.matchPassword(getPassword1, getPassword2);
                    if (result == 0){
                        presenter.changePassword(getPassword1);
                        alertDialog.dismiss();
                        showProgressBar();
                    } else if (result == 1){
                        Toast.makeText(Setting.this, getString(R.string.toast_unmatch_password), Toast.LENGTH_SHORT).show();
                    } else if (result == 2){
                        Toast.makeText(Setting.this, getString(R.string.toast_length_password), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        buttonBack = (Button) view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    public void successChangePassword() {
        hideProgressBar();
        Toast.makeText(this, getString(R.string.toast_updated_password), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorChangePassword() {
        hideProgressBar();
        Toast.makeText(this, getString(R.string.toast_account_not_created), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failedCredential() {
        hideProgressBar();
        Toast.makeText(this, getString(R.string.toast_invalid_credential), Toast.LENGTH_LONG).show();
    }

    @Override
    public void goLogin() {
        Toast.makeText(this, getString(R.string.toast_has_ben_delete_account), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            goHome();
        } else if (id == R.id.nav_profile) {
            goProfile();
        } else if (id == R.id.nav_history) {
            goHistory();
        } else if (id == R.id.nav_setting) {
            goSetting();
        } else if (id == R.id.nav_payment) {
            goPayment();
        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void goHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    @Override
    public void goHistory() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    @Override
    public void goSetting() {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }

    @Override
    public void goPayment() {
        Intent intent = new Intent(this, PaymentMethod.class);
        startActivity(intent);
    }

    @Override
    public void logOut() {
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
