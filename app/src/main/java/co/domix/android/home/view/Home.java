package co.domix.android.home.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.view.History;
import co.domix.android.customizer.view.PaymentMethod;
import co.domix.android.customizer.view.Profile;
import co.domix.android.customizer.view.Setting;
import co.domix.android.domiciliary.view.Domiciliary;
import co.domix.android.home.presenter.HomePresenter;
import co.domix.android.home.presenter.HomePresenterImpl;
import co.domix.android.login.view.Login;
import co.domix.android.services.LocationService;
import co.domix.android.user.view.User;
import co.domix.android.utils.ToastsKt;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeView, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    private FirebaseAuth firebaseAuth;
    private GoogleApiClient apiClient;
    private LocationManager locationManager;
    private LinearLayout linearRoot, linearNotInternet;
    private ProgressBar progressBar;
    private AlertDialog alert = null;
    private Button buttonGoUser, buttonGoDomiciliary, buttonRefresh;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private HomePresenter presenter;
    public DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_home));

        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Shrikhand-Regular.ttf");
        presenter = new HomePresenterImpl(this);
        app = (DomixApplication) getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progressBarHome);
        linearRoot = (LinearLayout) findViewById(R.id.rootLinearHome);
        linearNotInternet = (LinearLayout) findViewById(R.id.notInternetHome);
        buttonGoUser = (Button) findViewById(R.id.button_be_user);
        buttonGoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUser();
            }
        });

        buttonGoDomiciliary = (Button) findViewById(R.id.button_be_domiciliary);
        buttonGoDomiciliary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDomiciliary();
            }
        });

        buttonGoUser.setTypeface(font);
        buttonGoDomiciliary.setTypeface(font);

        buttonRefresh = (Button) findViewById(R.id.buttonRefreshHome);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                presenter.verifyLocationAndInternet(Home.this);
            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            onStart();
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
    public void goUser() {
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
    }

    @Override
    public void goDomiciliary() {
        Intent intent = new Intent(this, Domiciliary.class);
        startActivity(intent);
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
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void alertNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_location_deactivate)
                .setCancelable(false)
                .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home.super.finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void showNotInternet() {
        linearRoot.setVisibility(View.GONE);
        linearNotInternet.setVisibility(View.VISIBLE);
    }

    @Override
    public void showYesInternet() {
        linearNotInternet.setVisibility(View.GONE);
        linearRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public void logOut() {
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.verifyLocationAndInternet(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            Home.super.finish();
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
