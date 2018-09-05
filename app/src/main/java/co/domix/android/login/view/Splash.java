package co.domix.android.login.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.view.DomiciliaryScore;
import co.domix.android.domiciliary.view.OrderCatched;
import co.domix.android.home.view.Home;
import co.domix.android.login.presenter.SplashPresenter;
import co.domix.android.login.presenter.SplashPresenterImpl;
import co.domix.android.services.LocationService;
import co.domix.android.user.view.Requested;
import co.domix.android.user.view.UserScore;
import co.domix.android.utils.ToastsKt;

public class Splash extends AppCompatActivity implements SplashView, ActivityCompat.OnRequestPermissionsResultCallback {

    private ProgressBar progressBar;
    private AlertDialog alert = null;
    private DomixApplication app;
    private SplashPresenter presenter;

    //COMMENT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        app = (DomixApplication) getApplicationContext();
        presenter = new SplashPresenterImpl(this);
        progressBar = (ProgressBar) findViewById(R.id.prgBarSplash);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void queryStatePosition(String uid) {
        presenter.queryStatePosition(uid, this);
    }

    @Override
    public void goOrderCatched(int idOrder) {
        hideProgressBar();
        app.idOrder = idOrder;
        Intent intent = new Intent(this, OrderCatched.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goOrderRequested(int idOrder) {
        hideProgressBar();
        app.idOrder = idOrder;
        Intent intent = new Intent(this, Requested.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goUserScore(int idOrder) {
        hideProgressBar();
        app.idOrder = idOrder;
        Intent intent = new Intent(this, UserScore.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goDomiciliaryScore(int idOrder) {
        hideProgressBar();
        app.idOrder = idOrder;
        Intent intent = new Intent(this, DomiciliaryScore.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goHome() {
        hideProgressBar();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goLogin() {
        hideProgressBar();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void alertNoNetwork() {
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
                Splash.super.finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void notInternetConnection() {
        hideProgressBar();
        Toast.makeText(this, getResources().getString(R.string.text_no_internet_connection), Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permiso concedido
                    presenter.verifyNetworkAndInternet(this, app.isOnline(), app.firebaseUser, app.uId);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    ToastsKt.toastLong(Splash.this, getString(R.string.toast_impossible_give_permission));
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(Splash.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            presenter.verifyNetworkAndInternet(this, app.isOnline(), app.firebaseUser, app.uId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressBar();
//        Lo quité para que no se replique infinitamente la lectura de location, lo puse en onStart
//        presenter.verifyNetworkAndInternet(this, app.isOnline(), app.firebaseUser, app.uId);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
