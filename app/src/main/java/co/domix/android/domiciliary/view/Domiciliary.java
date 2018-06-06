package co.domix.android.domiciliary.view;

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
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Hashtable;
import java.util.List;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.presenter.DomiciliaryPresenter;
import co.domix.android.domiciliary.presenter.DomiciliaryPresenterImpl;
import co.domix.android.services.LocationService;
import co.domix.android.utils.ToastsKt;

/**
 * Created by unicorn on 11/12/2017.
 */

public class Domiciliary extends AppCompatActivity implements DomiciliaryView, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient apiClient;
    private LocationManager locationManager;
    private String country; //la, lo
    private int countIndex, idOrderToSend;
    private boolean fieldsWasFill;
    private ProgressBar progressBarDomiciliary;
    private Switch switchAB;
    private android.app.AlertDialog alertDialog;
    private TextInputEditText firstName, lastName, phone;
    private Button btnViewMap, btnAcceptDelivery, btnDismissDelivery, btnSendFullnameAndPhone, btnBack, buttonRefresh;
    private Hashtable<Integer, List> diccionario;
    private TextView tvAgo, tvFrom, tvTo, tvDimensions, tvDescription1, tvDescription2, waitinDeliveries, textRateUser, rateUser;
    private LinearLayout lnrSpiVehicle, lnrShowData, lnrNotInternet;
    private Spinner spiVehicle;
    private int vehSelected;
    private ScrollView scrollView;
    private AlertDialog alert = null;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private DomiciliaryPresenter presenter;
    private DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domiciliary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        app = (DomixApplication) getApplicationContext();
        presenter = new DomiciliaryPresenterImpl(this);

        switchAB = (Switch) findViewById(R.id.switchAB);
        switchAB.setChecked(false);
        lnrSpiVehicle = (LinearLayout) findViewById(R.id.lnrSpiVehicle);
        lnrNotInternet = (LinearLayout) findViewById(R.id.lnrNotInternet);
        scrollView = (ScrollView) findViewById(R.id.scrVieDomiciliary);
        progressBarDomiciliary = (ProgressBar) findViewById(R.id.prgBarDomiciliary);
        lnrShowData = (LinearLayout) findViewById(R.id.lnrShowData);
        waitinDeliveries = (TextView) findViewById(R.id.txtVieWaitingDeliveries);
        btnViewMap = (Button) findViewById(R.id.btnViewMap);
        btnAcceptDelivery = (Button) findViewById(R.id.btnAcceptRequest);
        btnDismissDelivery = (Button) findViewById(R.id.btnDismissRequest);

        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();
        editor.putBoolean(getString(R.string.const_sharedPref_key_searchDelivery), false);
//        editor.putBoolean("IsServiceActive", false);
        editor.commit();

        if (shaPref.getBoolean(getString(R.string.const_sharedPref_key_backfromServiceNotification), false)) {
            switchAB.setChecked(false);
            switchAB.setChecked(true);
            editor.putBoolean(getString(R.string.const_sharedPref_key_backfromServiceNotification), false);
            editor.commit();
        }

        spiVehicle = (Spinner) findViewById(R.id.spiVehicle);
        spiVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehSelected = (byte) spiVehicle.getSelectedItemId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (vehSelected == 0) {
                        ToastsKt.toastShort(Domiciliary.this, getString(R.string.toast_must_choise_vehicle));
                        switchAB.setChecked(false);
                    } else {
                        editor.putBoolean(getString(R.string.const_sharedPref_key_searchDelivery), true);
                        editor.commit();
                        lnrSpiVehicle.setVisibility(View.GONE);
                        waitinDeliveries.setVisibility(View.VISIBLE);
                        queryForFullnameAndPhone();
                    }
                } else {
                    waitinDeliveries.setVisibility(View.GONE);
                    lnrShowData.setVisibility(View.GONE);
                    lnrSpiVehicle.setVisibility(View.VISIBLE);
                    editor.putBoolean(getString(R.string.const_sharedPref_key_searchDelivery), false);
                    editor.commit();
                }
            }
        });

        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPreviewRouteOrder();
            }
        });

        btnAcceptDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                scrollView.setVisibility(View.GONE);
//                lnrShowData.setVisibility(View.GONE);
                presenter.sendDataDomiciliary(Domiciliary.this, idOrderToSend, app.uId, vehSelected, country);
            }
        });

        btnDismissDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart();
                switchAB.setChecked(false);
                waitinDeliveries.setVisibility(View.GONE);
                lnrShowData.setVisibility(View.GONE);
                lnrSpiVehicle.setVisibility(View.VISIBLE);
            }
        });
        buttonRefresh = (Button) findViewById(R.id.btnRefreshDomiciliary);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                presenter.verifyLocationAndInternet(Domiciliary.this);
            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.putBoolean(getString(R.string.const_sharedPref_key_searchDelivery), false);
        editor.commit();
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
                Domiciliary.super.finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void showYesInternet() {
        switchAB.setVisibility(View.VISIBLE);
        lnrNotInternet.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNotInternet() {
        hideProgressBar();
        switchAB.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        lnrNotInternet.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        progressBarDomiciliary.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarDomiciliary.setVisibility(View.GONE);
    }

    @Override
    public void searchDeliveries() {
        presenter.searchDeliveries(shaPref.getString(getString(R.string.const_sharedPref_key_lat_device), ""),
                                                      shaPref.getString(getString(R.string.const_sharedPref_key_lon_device), ""),
                                                      vehSelected);
    }

    @Override
    public void showResultOrder(Hashtable<Integer, List> dictionary, int countIndx) {
        diccionario = dictionary;
        countIndex = countIndx;
        country = dictionary.get(countIndex).get(10).toString();
        queryUserRate(dictionary.get(countIndex).get(0).toString());
        idOrderToSend = Integer.valueOf(dictionary.get(countIndex).get(0).toString());
        String sizeOrder = "";
        if ((dictionary.get(countIndex).get(8).toString()).equals("0")){
            sizeOrder = getString(R.string.text_letter);
        } else if ((dictionary.get(countIndex).get(8).toString()).equals("1")){
            sizeOrder = getString(R.string.text_bag);
        } else if ((dictionary.get(countIndex).get(8).toString()).equals("2")){
            sizeOrder = getString(R.string.text_trunk);
        } else if ((dictionary.get(countIndex).get(8).toString()).equals("3")){
            sizeOrder = getString(R.string.text_grid);
        }

        tvAgo.setText(dictionary.get(countIndex).get(1).toString());
        tvFrom.setText(dictionary.get(countIndex).get(2).toString());
        tvDimensions.setText(" " + sizeOrder);
        tvTo.setText(dictionary.get(countIndex).get(3).toString());
        tvDescription1.setText(dictionary.get(countIndex).get(4).toString());
        tvDescription2.setText(dictionary.get(countIndex).get(5).toString());
        waitinDeliveries.setVisibility(View.GONE);
        lnrShowData.setVisibility(View.VISIBLE);
    }

    @Override
    public void showResultNotOrder() {
        switchAB.setChecked(false);
        ToastsKt.toastLong(this, getString(R.string.toast_not_near_delivery));
    }

    @Override
    public void goPreviewRouteOrder() {
        editor.putBoolean(getString(R.string.const_sharedPref_key_searchDelivery), false);
        editor.commit();
        Intent intent = new Intent(this, PreviewRouteOrder.class);
        intent.putExtra("coordinateFromView", diccionario.get(countIndex).get(6).toString());
        intent.putExtra("coordinateToView", diccionario.get(countIndex).get(7).toString());
        startActivity(intent);
    }

    @Override
    public void responseOrderHasBeenTaken() {
        onStart();
        hideProgressBar();
        lnrShowData.setVisibility(View.VISIBLE);
        ToastsKt.toastLong(this, getString(R.string.toast_order_has_been_taken));
        waitinDeliveries.setVisibility(View.VISIBLE);
        lnrShowData.setVisibility(View.GONE);
    }

    @Override
    public void responseGoOrderCatched(String idOrder) {
        hideProgressBar();
        editor.putBoolean(getString(R.string.const_sharedPref_key_searchDelivery), false);
        app.idOrder = Integer.valueOf(idOrder);
//        editor.putString("latFrom", diccionario.get(countIndex).get(6).toString());
//        editor.putString("latTo", diccionario.get(countIndex).get(8).toString());
//        editor.putString("lonFrom", diccionario.get(countIndex).get(7).toString());
//        editor.putString("lonTo", diccionario.get(countIndex).get(9).toString());
//        editor.commit();
        Intent intent = new Intent(this, OrderCatched.class);
        startActivity(intent);
    }

    @Override
    public void queryForFullnameAndPhone() {
        presenter.queryForFullnameAndPhone(app.uId);
    }

    @Override
    public void openDialogSendContactData() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_send_user_contact, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSendFullnameAndPhone = view.findViewById(R.id.btnSendContactData);
        btnBack = view.findViewById(R.id.btnBack);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        phone = view.findViewById(R.id.phone);

        firstName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        lastName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );

        btnSendFullnameAndPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String getFirst_name = firstName.getText().toString();
                String getLast_name = lastName.getText().toString();
                String getPhone = phone.getText().toString();
                if (getFirst_name.equals("") || getLast_name.equals("") || getPhone.equals("")) {
                    Toast.makeText(Domiciliary.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    sendContactData(getFirst_name, getLast_name, getPhone);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    public void sendContactData(String firstName, String lastName, String phone) {
        presenter.sendContactData(app.uId, firstName, lastName, phone, this);
    }

    @Override
    public void contactDataSent() {
        onStart();
        Toast.makeText(this, R.string.toast_sent_contact_data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void queryUserRate(String idOrder) {
        presenter.queryUserRate(idOrder);
    }

    @Override
    public void responseQueryRate(String rate) {
        textRateUser = (TextView) findViewById(R.id.txtVieYourRating);
        rateUser = (TextView) findViewById(R.id.txtVieRateUser);
        if (!rate.equals("0.00")) {
            rateUser.setText(rate);
        } else {
            rateUser.setText(getString(R.string.text_new));
        }
    }

    @Override
    public void responseForFullnameAndPhone(boolean result) {
        if (shaPref.getBoolean(getString(R.string.const_sharedPref_key_searchDelivery), false)) {
            fieldsWasFill = result;
            if (!fieldsWasFill) {
                openDialogSendContactData();
            } else {
                searchDeliveries();
            }
        }
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
            Log.w("jjj", "Domiciliary Latitu-> "+loc.getLatitude());
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
                            Domiciliary.super.finish();
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

    @Override
    protected void onStart() {
        super.onStart();
        presenter.verifyLocationAndInternet(this);

        lnrShowData = (LinearLayout) findViewById(R.id.lnrShowData);
        tvAgo = (TextView) findViewById(R.id.txtVieAgo);
        tvFrom = (TextView) findViewById(R.id.txtVieFrom);
        tvTo = (TextView) findViewById(R.id.txtVieTo);
        tvDimensions = (TextView) findViewById(R.id.txtVieDimensions);
        tvDescription1 = (TextView) findViewById(R.id.txtVieDescription1);
        tvDescription2 = (TextView) findViewById(R.id.txtVieDescription2);
        waitinDeliveries = (TextView) findViewById(R.id.txtVieWaitingDeliveries);
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
}
