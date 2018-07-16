package co.domix.android.login.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.login.presenter.SignupPresenter;
import co.domix.android.login.presenter.SignupPresenterImpl;
import co.domix.android.utils.ToastsKt;

public class Signup extends AppCompatActivity implements SignupView, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient apiClient;
    private LocationManager locationManager;
    private AlertDialog alert = null;
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

        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();

        progressBar = (ProgressBar) findViewById(R.id.prgBarSignup);
        emailFieldForSignup = (TextInputEditText) findViewById(R.id.txtInpEmailSignup);
        passwordFieldForSignup = (TextInputEditText) findViewById(R.id.txtInpPasswordSignup);
        confirmPasswordFieldForSignup = (TextInputEditText) findViewById(R.id.txtInpConfirmPasswordSignup);
        buttonSignup = (Button) findViewById(R.id.btnSignup);
        buttonBack = (Button) findViewById(R.id.btnBack);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showProgressBar();
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

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
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
            hideProgressBar();
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
        presenter.signup(email, password, confirmPassword, shaPref.getString(getString(R.string.const_sharedPref_key_lat_device), ""),
                        shaPref.getString(getString(R.string.const_sharedPref_key_lon_device), ""), this, firebaseAuth);
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
                    builder.setMessage("Desactivado curiosamente el GPS")
                            .setCancelable(false)
                            .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            }).setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Signup.super.finish();
                        }
                    });
                    alert = builder.create();
                    alert.show();
                }
            } catch (Exception e){
                ToastsKt.toastShort(this, "Ocurrió un error con tu GPS");
            }

        }
    }
}
