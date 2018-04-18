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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.directionModule.DirectionFinder;
import co.domix.android.directionModule.DirectionFinderListener;
import co.domix.android.directionModule.Route;
import co.domix.android.domiciliary.presenter.DomiciliaryPresenter;
import co.domix.android.domiciliary.presenter.DomiciliaryPresenterImpl;
import co.domix.android.domiciliary.service.NotificationService;
import co.domix.android.utils.ToastsKt;

/**
 * Created by unicorn on 11/12/2017.
 */

public class Domiciliary extends AppCompatActivity implements DomiciliaryView, LocationListener {

    protected LocationManager locationManager;
    private String la, lo;
    private int distMin, countForDictionary, countIndex, countIndexTemp, countChilds, idOrderToSend;
    private boolean fieldsWasFill;
    private ProgressBar progressBarDomiciliary;
    private Switch switchAB;
    private android.app.AlertDialog alertDialog;
    private TextInputEditText firstName, lastName, phone;
    private Button btnViewMap, btnAcceptDelivery, btnDismissDelivery, buttonSendFullnameAndPhone, buttonRefresh;
    private Hashtable<Integer, List> diccionario;
    private TextView tvAgo, tvFrom, tvTo, tvDimensions, tvDescription1, tvDescription2, waitinDeliveries, textRateUser, rateUser;
    private LinearLayout lnrSpiVehicle, lnrShowData, lnrNotInternet;
    private Spinner spiVehicle;
    private int vehSelected;
    private ScrollView scrollView;
    private AlertDialog alert = null;
    private SharedPreferences location;
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        switchAB = (Switch) findViewById(R.id.switchAB);
        lnrSpiVehicle = (LinearLayout) findViewById(R.id.lnrSpiVehicle);
        lnrNotInternet = (LinearLayout) findViewById(R.id.lnrNotInternet);
        scrollView = (ScrollView) findViewById(R.id.scrVieDomiciliary);
        progressBarDomiciliary = (ProgressBar) findViewById(R.id.prgBarDomiciliary);
        lnrShowData = (LinearLayout) findViewById(R.id.lnrShowData);
        waitinDeliveries = (TextView) findViewById(R.id.txtVieWaitingDeliveries);
        btnViewMap = (Button) findViewById(R.id.btnViewMap);
        btnAcceptDelivery = (Button) findViewById(R.id.btnAcceptRequest);
        btnDismissDelivery = (Button) findViewById(R.id.btnDismissRequest);

        location = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = location.edit();
        editor.putBoolean("SearchDelivery", false);
        editor.putBoolean("IsServiceActive", false);
        editor.commit();

        if (location.getBoolean("backFromServiceNotification", false)) {
            switchAB.setChecked(false);
            switchAB.setChecked(true);
            editor.putBoolean("backFromServiceNotification", false);
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
                    if (vehSelected == 0){
                        ToastsKt.toastShort(Domiciliary.this, getString(R.string.toast_must_choise_vehicle));
                        switchAB.setChecked(false);
                    } else {
                        editor.putBoolean("SearchDelivery", true);
                        editor.commit();
                        lnrSpiVehicle.setVisibility(View.GONE);
                        waitinDeliveries.setVisibility(View.VISIBLE);
                        queryForFullnameAndPhone();
                    }
                } else {
                    editor.putBoolean("SearchDelivery", false);
                    editor.commit();
                    Domiciliary.super.recreate();
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
                lnrShowData.setVisibility(View.GONE);
                showProgressBar();
                presenter.sendDataDomiciliary(Domiciliary.this, idOrderToSend, app.uId, vehSelected);
            }
        });

        btnDismissDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart();
                switchAB.setChecked(false);
                switchAB.setChecked(true);
                waitinDeliveries.setVisibility(View.VISIBLE);
                lnrShowData.setVisibility(View.GONE);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.putBoolean("SearchDelivery", false);
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
        presenter.searchDeliveries(la, lo, vehSelected);
    }

    @Override
    public void showResultOrder(Hashtable<Integer, List> dictionary, int countIndex) {
        diccionario = dictionary;
        queryUserRate(dictionary.get(countIndex).get(0).toString());
        idOrderToSend = Integer.valueOf(dictionary.get(countIndex).get(0).toString());
        String sizeOrder = "";
        if ((dictionary.get(countIndex).get(10).toString()).equals("0")){
            sizeOrder = getString(R.string.text_letter);
        } else if ((dictionary.get(countIndex).get(10).toString()).equals("1")){
            sizeOrder = getString(R.string.text_bag);
        } else if ((dictionary.get(countIndex).get(10).toString()).equals("2")){
            sizeOrder = getString(R.string.text_trunk);
        } else if ((dictionary.get(countIndex).get(10).toString()).equals("3")){
            sizeOrder = getString(R.string.text_grid);
        }

        tvAgo.append(" " + dictionary.get(countIndex).get(1).toString());
        tvFrom.append(" " + dictionary.get(countIndex).get(2).toString());
        tvDimensions.append(" " + sizeOrder);
        tvTo.append(" " + dictionary.get(countIndex).get(3).toString());
        tvDescription1.append(" " + dictionary.get(countIndex).get(4).toString());
        tvDescription2.append(" " + dictionary.get(countIndex).get(5).toString());
        waitinDeliveries.setVisibility(View.GONE);
        lnrShowData.setVisibility(View.VISIBLE);
    }

    @Override
    public void goPreviewRouteOrder() {
        editor.putBoolean("SearchDelivery", false);
        editor.commit();
        Intent intent = new Intent(this, PreviewRouteOrder.class);
        intent.putExtra("latFrom", diccionario.get(countIndex).get(6).toString());
        intent.putExtra("latTo", diccionario.get(countIndex).get(8).toString());
        intent.putExtra("lonFrom", diccionario.get(countIndex).get(7).toString());
        intent.putExtra("lonTo", diccionario.get(countIndex).get(9).toString());
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
        editor.putBoolean("SearchDelivery", false);
        app.idOrder = Integer.valueOf(idOrder);
        editor.putString("latFrom", diccionario.get(countIndex).get(6).toString());
        editor.putString("latTo", diccionario.get(countIndex).get(8).toString());
        editor.putString("lonFrom", diccionario.get(countIndex).get(7).toString());
        editor.putString("lonTo", diccionario.get(countIndex).get(9).toString());
        editor.commit();
        Intent intent = new Intent(this, OrderCatched.class);
        startActivity(intent);
    }

    @Override
    public void queryForFullnameAndPhone() {
        distMin = 0;
        countForDictionary = 0;
        countIndex = 0;
        countIndexTemp = 0;
        presenter.queryForFullnameAndPhone(app.uId);
    }

    @Override
    public void openDialogSendContactData() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_send_user_contact, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buttonSendFullnameAndPhone = (Button) view.findViewById(R.id.buttonSendContactData);
        firstName = (TextInputEditText) view.findViewById(R.id.firstName);
        lastName = (TextInputEditText) view.findViewById(R.id.lastName);
        phone = (TextInputEditText) view.findViewById(R.id.phone);

        firstName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        lastName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );

        buttonSendFullnameAndPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String getFirstName = firstName.getText().toString();
                String getLastName = lastName.getText().toString();
                String getPhone = phone.getText().toString();
                if (getFirstName.equals("") || getLastName.equals("") || getPhone.equals("")) {
                    Toast.makeText(Domiciliary.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    sendContactData(getFirstName, getLastName, getPhone);
                }
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
        if (location.getBoolean("SearchDelivery", false)) {
            fieldsWasFill = result;
            if (!fieldsWasFill) {
                openDialogSendContactData();
            } else {
                searchDeliveries();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this, NotificationService.class));

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
        presenter.verifyLocationAndInternet(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Domiciliary.super.recreate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ((location.getBoolean("SearchDelivery", false))) {
            startService(new Intent(this, NotificationService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        la = String.valueOf(location.getLatitude());
        lo = String.valueOf(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
