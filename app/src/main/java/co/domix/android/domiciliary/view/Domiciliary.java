package co.domix.android.domiciliary.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import co.domix.android.domiciliary.service.CoordinateService;
import co.domix.android.domiciliary.service.NotificationService;

import static java.lang.Thread.sleep;

/**
 * Created by unicorn on 11/12/2017.
 */

public class Domiciliary extends AppCompatActivity implements DomiciliaryView, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, DirectionFinderListener {

    private String la, lo;
    private int distMin;
    private int countForDictionary;
    private int countIndex;
    private int countIndexTemp;
    private int countChilds;
    private int idOrderToSend;
    private boolean fieldsWasFill;
    private DomixApplication app;
    private LocationManager locationManager;
    private Switch switchAB;
    private android.app.AlertDialog alertDialog;
    private TextInputEditText firstName, lastName, phone;
    private Button btnViewMap, btnAcceptDelivery, btnDismissDelivery, buttonSendFullnameAndPhone;
    private List<String> listica;
    private Hashtable<Integer, List> diccionario;
    private TextView tvAgo, tvFrom, tvTo, tvDescription1, tvDescription2, waitinDeliveries, textRateUser, rateUser;
    private LinearLayout linearLayout;
    private LocationRequest locRequest;
    private GoogleApiClient apiClient;
    private AlertDialog alert = null;
    private SharedPreferences location;
    private SharedPreferences.Editor editor;
    private List<Marker> originMarkers = new ArrayList<>(), destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private DomiciliaryPresenter presenter;

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
        linearLayout = (LinearLayout) findViewById(R.id.show_data);
        waitinDeliveries = (TextView) findViewById(R.id.waiting_deliveries);
        btnViewMap = (Button) findViewById(R.id.buttonViewMap);
        btnAcceptDelivery = (Button) findViewById(R.id.buttonAcceptRequest);
        btnDismissDelivery = (Button) findViewById(R.id.buttonDismissRequest);

        location = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = location.edit();
        editor.putBoolean("SearchDelivery", false);
        editor.putBoolean("IsServiceActive", false);
        editor.commit();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        enableLocationUpdates();

        if (location.getBoolean("backFromServiceNotification", false)){
            switchAB.setChecked(false);
            switchAB.setChecked(true);
            editor.putBoolean("backFromServiceNotification", false);
            editor.commit();
        }

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("SearchDelivery", true);
                    editor.commit();
                    waitinDeliveries.setVisibility(View.VISIBLE);
                    queryForFullnameAndPhone();
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
                sendDataDomiciliary();
            }
        });

        btnDismissDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart();
                switchAB.setChecked(false);
                switchAB.setChecked(true);
                waitinDeliveries.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
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
    public void searchDeliveries() {
        presenter.searchDeliveries();
    }

    private void enableLocationUpdates() {
        locRequest = new LocationRequest();
        locRequest.setInterval(500);
        locRequest.setFastestInterval(400);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locRequest)
                .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(Domiciliary.this, 201);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, this);
        }
    }

    @Override
    public void goCompareDistance(int idOrder, String ago, String from, String to, String description1,
                                  String description2, String oriLat, String oriLon, String desLat,
                                  String desLon) {
        diccionario = new Hashtable<Integer, List>();
        listica = new ArrayList<String>();
        String idOrderStr = String.valueOf(idOrder);
        listica.add(idOrderStr);
        listica.add(ago);
        listica.add(from);
        listica.add(to);
        listica.add(description1);
        listica.add(description2);
        listica.add(oriLat);
        listica.add(oriLon);
        listica.add(desLat);
        listica.add(desLon);

        diccionario.put(countForDictionary, listica);

        try {
            String uno = oriLat + ", " + oriLon;
            String dos = la + ", " + lo;
            new DirectionFinder(this, uno, dos).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        countForDictionary++;
    }

    @Override
    public void countChild(int countChild) {
        countChilds = countChild;
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
    public void sendDataDomiciliary() {
        presenter.sendDataDomiciliary(this, idOrderToSend, app.uId);
    }

    @Override
    public void responseOrderHasBeenTaken() {
        onStart();
        waitinDeliveries.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    public void responseGoOrderCatched(String idOrder) {
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
        textRateUser = (TextView) findViewById(R.id.textYourRating);
        rateUser = (TextView) findViewById(R.id.rateUser);
        if (!rate.equals("0.00")) {
            rateUser.setText(rate);
        } else {
            rateUser.setText(getString(R.string.text_new));
        }
    }

    private void alertNoGps() {
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
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        //if (location.getBoolean("SearchDelivery", false)) {
            for (Route route : routes) {
                int newDistance = route.distance.value;
                if (distMin != 0) {
                    if (distMin > newDistance) {
                        distMin = newDistance;
                        countIndex = countIndexTemp;
                    }
                } else {
                    distMin = newDistance;
                }
                countIndexTemp++;
            }

            if (countIndexTemp == countChilds) {
                queryUserRate(diccionario.get(countIndex).get(0).toString());
                idOrderToSend = Integer.valueOf(diccionario.get(countIndex).get(0).toString());
                tvAgo.append(" " + diccionario.get(countIndex).get(1).toString());
                tvFrom.append(" " + diccionario.get(countIndex).get(2).toString());
                tvTo.append(" " + diccionario.get(countIndex).get(3).toString());
                tvDescription1.append(" " + diccionario.get(countIndex).get(4).toString());
                tvDescription2.append(" " + diccionario.get(countIndex).get(5).toString());
                waitinDeliveries.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        //}
    }

    @Override
    public void onLocationChanged(Location location) {
        la = String.valueOf(location.getLatitude());
        lo = String.valueOf(location.getLongitude());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this, NotificationService.class));

        linearLayout = (LinearLayout) findViewById(R.id.show_data);
        tvAgo = (TextView) findViewById(R.id.d_ago);
        tvFrom = (TextView) findViewById(R.id.d_from);
        tvTo = (TextView) findViewById(R.id.d_to);
        tvDescription1 = (TextView) findViewById(R.id.d_description1);
        tvDescription2 = (TextView) findViewById(R.id.d_description2);
        waitinDeliveries = (TextView) findViewById(R.id.waiting_deliveries);

        //enableLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!network_enabled) {
            alertNoGps();
        }
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
}
