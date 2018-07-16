package co.domix.android.user.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.view.Profile;
import co.domix.android.services.LocationService;
import co.domix.android.user.presenter.UserPresenter;
import co.domix.android.user.presenter.UserPresenterImpl;
import co.domix.android.utils.ToastsKt;

public class User extends AppCompatActivity implements UserView, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient apiClient;
    private LocationManager locationManager;
    private ScrollView scrollView;
    private RadioGroup radioGroup;
    private LinearLayout linearNotInternet, lnrSwiCredit, lnrPaymentMethod;
    private SwitchCompat switchCompat;
    private Button buttonRequestOrder, btnSendFullnameAndPhone, btnBack, buttonRefresh;
    private TextView txtFrom, txtTo, buttonSelectFrom, buttonSelectTo, paymentCash;
    private EditText description1, description2;
    private Spinner spiDimensions;
    private byte dimenSelected;
    private TextInputEditText firstName, lastName, phone;
    private String countryOrigen, cityOrigen, codeCountry;
    private byte payMethod;
    private int totalCostToDB, priceInCash, disBetweenPoints, mCredit, costDelDesCredit,
            updateCreditUserToDB, creditUsedToDB;
    private ProgressBar progressBarRequest;
    private AlertDialog alert = null;
    private boolean radioGroupActive, fieldsWasFill;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private DecimalFormat formatMiles = new DecimalFormat("###,###.##");
    private DomixApplication app;
    private UserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.text_make_your_order);
        presenter = new UserPresenterImpl(this);
        app = (DomixApplication) getApplicationContext();

        presenter.queryPersonalDataFill(app.uId);

        scrollView = (ScrollView) findViewById(R.id.rootScroll);
        lnrSwiCredit = (LinearLayout) findViewById(R.id.lnrSwiCredit);
        lnrPaymentMethod = (LinearLayout) findViewById(R.id.lnrPaymentMethod);
        switchCompat = (SwitchCompat) findViewById(R.id.swiCredit);
        radioGroupActive = false;
        linearNotInternet = (LinearLayout) findViewById(R.id.notInternetUser);
        progressBarRequest = (ProgressBar) findViewById(R.id.progressBarRequest);

        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();

        spiDimensions = (Spinner) findViewById(R.id.spinnerDimensions);
        spiDimensions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dimenSelected = (byte) spiDimensions.getSelectedItemId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.rdGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.payWithCash:
                        radioGroupActive = true;
                        payMethod = 0;
                        break;
                    case R.id.payWithCredit:
                        radioGroupActive = true;
                        payMethod = 1;
                        break;
                }
            }
        });

        description1 = (EditText) findViewById(R.id.idOrderDescription1);
        description2 = (EditText) findViewById(R.id.idOrderDescription2);
        paymentCash = (TextView) findViewById(R.id.idMoneyToPay);

        buttonSelectFrom = (TextView) findViewById(R.id.buttonSelectFrom);
        buttonSelectFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(false);
                editor.putInt("whatAddress", 0);
                editor.commit();
                goPickMap();
            }
        });

        buttonSelectTo = (TextView) findViewById(R.id.buttonSelectTo);
        buttonSelectTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(false);
                editor.putInt("whatAddress", 1);
                editor.commit();
                goPickMap();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    costDelDesCredit = priceInCash - mCredit;
                    if (costDelDesCredit <= 0){
                        paymentCash.setText(" 0.00 " + codeCountry);
                        totalCostToDB = 0;
                        updateCreditUserToDB = costDelDesCredit * -1;
                        creditUsedToDB = mCredit - updateCreditUserToDB;
                        lnrPaymentMethod.setVisibility(View.GONE);
                        payMethod = 2;
                    } else {
                        paymentCash.setText(" " + formatMiles.format(costDelDesCredit) + " " + codeCountry);
                        totalCostToDB = costDelDesCredit;
                        updateCreditUserToDB = 0;
                        creditUsedToDB = mCredit;
                        lnrPaymentMethod.setVisibility(View.VISIBLE);
                    }
                } else {
                    lnrPaymentMethod.setVisibility(View.VISIBLE);
                    radioGroup.clearCheck();
                    radioGroupActive = false;
                    totalCostToDB = priceInCash;
                    paymentCash.setText(" " + formatMiles.format(priceInCash) + " " + codeCountry);
                }
            }
        });

        buttonRequestOrder = (Button) findViewById(R.id.buttonRequestOrder);
        buttonRequestOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.GONE);
                showProgressBar();
                int creditApplyToDB, updtCreditUserToDB;
                if (switchCompat.isChecked()){
                    creditApplyToDB = creditUsedToDB;
                    updtCreditUserToDB = updateCreditUserToDB;
                } else {
                    creditApplyToDB = 0;
                    updtCreditUserToDB = 0;
                }
                if (radioGroupActive){
                    presenter.request(fieldsWasFill, app.uId, app.email, countryOrigen, cityOrigen,
                            txtFrom.getText().toString(), txtTo.getText().toString(), disBetweenPoints,
                            description1.getText().toString(), description2.getText().toString(),
                            dimenSelected, payMethod, totalCostToDB, creditApplyToDB, updtCreditUserToDB, User.this);
                } else {
                    if (payMethod == 2){
                        presenter.request(fieldsWasFill, app.uId, app.email, countryOrigen, cityOrigen,
                                txtFrom.getText().toString(), txtTo.getText().toString(), disBetweenPoints,
                                description1.getText().toString(), description2.getText().toString(),
                                dimenSelected, payMethod, totalCostToDB, creditApplyToDB, updtCreditUserToDB, User.this);
                    } else {
                        scrollView.setVisibility(View.VISIBLE);
                        hideProgressBar();
                        ToastsKt.toastShort(User.this, getString(R.string.toast_choose_payment_method));
                    }
                }
            }
        });

        buttonRefresh = (Button) findViewById(R.id.buttonRefreshUser);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                presenter.verifyLocationAndInternet(User.this);
            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void responseQueryPersonalDataFill(boolean fillData) {
        fieldsWasFill = fillData;
        if (!fillData){
            new AlertDialog.Builder(this)
                    .setPositiveButton(getString(R.string.message_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(User.this, Profile.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.message_no), null)
                    .setMessage(getString(R.string.text_message_fill_in_data))
                    .show();
        }
    }

    @Override
    public void messageDataNotFill(boolean showAlert) {
        scrollView.setVisibility(View.VISIBLE);
        hideProgressBar();
        responseQueryPersonalDataFill(showAlert);
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
                User.super.finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void responseSuccessRequest(int getCountFull) {
        app.idOrder = getCountFull;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editor.putBoolean(getString(R.string.const_sharedPref_key_charge_after_two_minutte), false);
                        editor.commit();
                        hideProgressBar();
                        Intent intent = new Intent(User.this, Requested.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void countryNotAvailable() {
        ToastsKt.toastLong(this, getString(R.string.toast_country_not_available));
    }

    @Override
    public void resultErrorRequest() {
        scrollView.setVisibility(View.VISIBLE);
        hideProgressBar();
        Toast.makeText(this, R.string.toast_error_request, Toast.LENGTH_LONG).show();
    }

    @Override
    public void goPickMap() {
        Intent intent = new Intent(this, PickMap.class);
        startActivity(intent);
    }

    @Override
    public void responseFromName(String fromm) {
        txtFrom.setText(fromm);
    }

    @Override
    public void responseToName(String too) {
        txtTo.setText(too);
    }

    @Override
    public void responseEmptyFields(String toastMessage) {
        hideProgressBar();
        scrollView.setVisibility(View.VISIBLE);
        ToastsKt.toastShort(this, toastMessage);
    }

    @Override
    public void responseCash(int priceInCashh, String countryO, String countryOrigenn, String cityOrigenn,
                             int distanceBetweenPoints, int myCredit) {
        mCredit = myCredit;
        codeCountry = countryO;
        if (myCredit > 0){
            lnrSwiCredit.setVisibility(View.VISIBLE);
            switchCompat.setHint(getString(R.string.text_use_credit) + " " + myCredit + " " + countryO);
        }
        countryOrigen = countryOrigenn;
        cityOrigen = cityOrigenn;
        priceInCash = priceInCashh;
        totalCostToDB = priceInCashh;
        disBetweenPoints = distanceBetweenPoints;
        paymentCash.setText(" " + formatMiles.format(priceInCash) + " " + codeCountry);
    }

    @Override
    public void showNotInternet() {
        hideProgressBar();
        scrollView.setVisibility(View.GONE);
        linearNotInternet.setVisibility(View.VISIBLE);
    }

    @Override
    public void showYesInternet() {
        try {
            linearNotInternet.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        } catch (Exception e){

        }
    }

    @Override
    public void showProgressBar() {
        progressBarRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        try {
            progressBarRequest.setVisibility(View.GONE);
        } catch (Exception e){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.countriesAvailable();
        presenter.verifyLocationAndInternet(this);
        txtFrom = (TextView) findViewById(R.id.idFrom);
        txtTo = (TextView) findViewById(R.id.idTo);
        if (!switchCompat.isChecked()){
            try {
                presenter.requestGeolocationAndDistance(app.uId, shaPref.getString("latFrom", ""),
                        shaPref.getString("lonFrom", ""),
                        shaPref.getString("latTo", ""),
                        shaPref.getString("lonTo", ""),
                        shaPref.getInt("whatAddress", 2),
                        this);
            } catch (Exception e){

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        presenter.verifyLocationAndInternet(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.remove("latFrom");
        editor.remove("lonFrom");
        editor.remove("latTo");
        editor.remove("lonTo");
        editor.commit();
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
                            User.super.finish();
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
