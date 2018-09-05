package co.domix.android.login.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import co.domix.android.utils.ToastsKt;

public class Login extends AppCompatActivity implements LoginView, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient apiClient;
    private LocationManager locationManager;
    private AlertDialog alert = null;
    private TextInputEditText emailField, passwordField, emailFieldForRestore;
    private Button buttonSignin, buttonSignup, buttonRestore, buttonBack;
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

        restorePassword = (TextView) findViewById(R.id.txtVieForgotMyPassword);
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

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
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
    public void signinError(String msg) {
        firebaseAuth.getInstance().signOut();
        enableInputs();
        hideProgressBar();
        if (msg == "The password is invalid or the user does not have a password."){
            ToastsKt.toastShort(this, getString(R.string.toast_signin_invalid_password));
        } else if (msg == "There is no user record corresponding to this identifier. The user may have been deleted."){
            ToastsKt.toastShort(this, getString(R.string.toast_signin_user_not_registered));
        }
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

        emailFieldForRestore = view.findViewById(R.id.txtInpEmailForReset);

        buttonBack = view.findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        buttonRestore = view.findViewById(R.id.btnResetPassword);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        } else {
            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);
            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);
                updateUI(lastLocation);
            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
            }
        }
    }

    private void updateUI(Location loc) {
        if (loc != null) {
            SharedPreferences location = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = location.edit();
            editor.putString(getString(R.string.const_sharedPref_key_lat_device), String.valueOf(loc.getLatitude()));
            editor.putString(getString(R.string.const_sharedPref_key_lon_device), String.valueOf(loc.getLongitude()));
            editor.commit();
        } else {
            try {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // Unknown Latitude and Longitude
                    // Available GPS but not recognize coordenates
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.message_curiosly_no_detected_your_gps))
                            .setCancelable(false)
                            .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            }).setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Login.super.finish();
                        }
                    });
                    alert = builder.create();
                    alert.show();
                }
            } catch (Exception e){
                ToastsKt.toastShort(this, getString(R.string.toast_ocurred_an_error_with_gps));
            }

        }
    }
}
