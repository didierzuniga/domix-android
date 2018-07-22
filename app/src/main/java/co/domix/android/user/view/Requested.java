package co.domix.android.user.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.directionModule.DirectionFinder;
import co.domix.android.directionModule.DirectionFinderListener;
import co.domix.android.directionModule.Route;
import co.domix.android.services.IncomingDeliveryman;
import co.domix.android.services.PayToCancel;
import co.domix.android.user.presenter.RequestedPresenter;
import co.domix.android.user.presenter.RequestedPresenterImpl;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by unicorn on 11/12/2017.
 */

public class Requested extends AppCompatActivity implements RequestedView, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, DirectionFinderListener {

    private LinearLayout linearParent;
    private TextView textViewWaitingDomiciliary, textViewRateDomiciliary,
            textViewSelectedDomiciliary, textViewDataDomiciliary;
    private ImageView ivVehicle;
    private CircleImageView ivDeliveryman;
    private String idDomiciliary;
    private Button buttonCanceled;
    private boolean locDomi, validateDomiciliaryRealtime = false, initialize = false;
    private Marker m2;
    private byte g = 0;
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private LocationRequest locRequest;
    private List<Marker> originMarkers = new ArrayList<>(), destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private StorageReference storageReference;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private DomixApplication app;
    private RequestedPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_requested));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        presenter = new RequestedPresenterImpl(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();

        app = (DomixApplication) getApplicationContext();
        storageReference = FirebaseStorage.getInstance().getReference();

        linearParent = (LinearLayout) findViewById(R.id.linearParent);
        textViewWaitingDomiciliary = (TextView) findViewById(R.id.waiting_domiciliary);
        textViewRateDomiciliary = (TextView) findViewById(R.id.rateDomiciliary);
        ivVehicle = (ImageView) findViewById(R.id.ivVehicleUsed);
        ivDeliveryman = (CircleImageView) findViewById(R.id.imageProfileDeliveryman);
        textViewSelectedDomiciliary = (TextView) findViewById(R.id.selectedDomiciliary);
        textViewDataDomiciliary = (TextView) findViewById(R.id.dataDomiciliary);
        buttonCanceled = (Button) findViewById(R.id.buttonCanceledRequest);
        buttonCanceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el usuario cancela muestra mensaje que s hará cobro de tarifa minima
                if (shaPref.getBoolean(getString(R.string.const_sharedPref_key_charge_after_two_minutte), false)){
                    dialogShowChargeInfo();
                } else {
                    dialogCancel(false);
                }
            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        enableLocationUpdates();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        editor.putBoolean(getString(R.string.const_sharedPref_key_charge_after_two_minutte), true);
                        editor.commit();
                    }
                }, new IntentFilter(PayToCancel.ACTION_COUNTER_BUTTON)
        );
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.toast_can_not_backpressed), Toast.LENGTH_SHORT).show();
    }

    private void dialogShowChargeInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_cancel_request_after_two_minute);
        builder.setPositiveButton(R.string.message_accept,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogCancel(true);
                    }
                }
        )
                .setNegativeButton(R.string.message_back, null);
        builder.create().show();
    }

    private void enableLocationUpdates() {
        locRequest = new LocationRequest();
        locRequest.setInterval(1000);
        locRequest.setFastestInterval(900);
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
                        // Configuración correcta
                        // startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Se requiere actuación del usuario
                            status.startResolutionForResult(Requested.this, 201);
                        } catch (IntentSender.SendIntentException e) {
                            // btnActualizar.setChecked(false);
                            // Error al intentar solucionar configuración de ubicación
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // No se puede cumplir la configuración de ubicación necesaria
                        // btnActualizar.setChecked(false);
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//      Conectado correctamente a Google Play Services
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LatLng myPos = new LatLng(0, 0);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));

        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void listenForUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.listenForUpdate(app.idOrder, Requested.this);
                    }
                }, 1500);
            }
        });
    }


    @Override
    public void responseDomiciliaryCatched(String id, String rate, String name, String cellPhone, int usedVehicle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startService(new Intent(this, PayToCancel.class));
        } else {
            startService(new Intent(this, PayToCancel.class));
        }
        idDomiciliary = id;
        executeGlide();
        if (!rate.equals("0.00") || !rate.equals("0,00")){
            textViewRateDomiciliary.setText(rate);
        } else {
            textViewRateDomiciliary.setText(getString(R.string.text_new));
        }
        linearParent.setVisibility(View.VISIBLE);
        textViewSelectedDomiciliary.setText(name);
        textViewDataDomiciliary.setText(cellPhone);
        if (usedVehicle == 1){
            ivVehicle.setImageResource(R.drawable.ic_bicycle);
        } else if (usedVehicle == 2){
            ivVehicle.setImageResource(R.drawable.ic_bike);
        }
        else if (usedVehicle == 3){
            ivVehicle.setImageResource(R.drawable.ic_car);
        }
        textViewWaitingDomiciliary.setVisibility(View.GONE);
        locDomi = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateDomiPosition();
                    }
                }, 2000);
            }
        });
    }

    public void executeGlide() {
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageReference.child("image_profile/" + idDomiciliary + "/img1.png"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(500, 500)
                .centerCrop()
                .into(ivDeliveryman);
    }

    @Override
    public void dialogCancel(boolean afterTwoMinutes) {
        presenter.dialogCancel(afterTwoMinutes, app.uId, app.idOrder, this);
    }

    @Override
    public void resultGoUserActivity() {
        editor.putBoolean(getString(R.string.const_sharedPref_key_charge_after_two_minutte), true);
        editor.commit();
        app.idOrder = 0;
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goRateUser(int resultEarnedCredit, String currencyCode) {
        editor.putBoolean(getString(R.string.const_sharedPref_key_charge_after_two_minutte), true);
        editor.commit();
        Intent intent = new Intent(this, UserScore.class);
        intent.putExtra("earnedCredit", resultEarnedCredit);
        intent.putExtra("currencyCode", currencyCode);
        startActivity(intent);
        finish();
    }

    @Override
    public void resultCoordinatesFromTo(String origenCoordinate, String destineCoordinate) {
        try {
            String uno = origenCoordinate;
            String dos = destineCoordinate;
            if (initialize == false) {
                new DirectionFinder(this, uno, dos).execute();
                initialize = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDomiPosition() {
        presenter.updateDomiPosition(app.idOrder, this);
    }

    @Override
    public void responseCoordDomiciliary(double latDomiciliary, double lonDomiciliary) {
        domiLocated(latDomiciliary, lonDomiciliary);
    }

    @Override
    public void resultNotCatched() {
        locDomi = false;
        textViewSelectedDomiciliary.setText("");
        textViewDataDomiciliary.setText("");
        textViewWaitingDomiciliary.setVisibility(View.VISIBLE);
        linearParent.setVisibility(View.GONE);
        if (validateDomiciliaryRealtime) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                stopService(new Intent(this, PayToCancel.class));
            } else {
                stopService(new Intent(this, PayToCancel.class));
            }
            editor.putBoolean(getString(R.string.const_sharedPref_key_charge_after_two_minutte), false);
            editor.commit();
            validateDomiciliaryRealtime = false;
            m2.remove();
            g = 0;
            Toast.makeText(this, getResources().getString(R.string.toast_domiciliary_has_cancelled_order), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void domiLocated(double latDomiciliary, double lonDomiciliary) {
        // Aqui obtengo Lat,Lon del domiciliario en FirebaseDatabase
        LatLng yourPo = new LatLng(latDomiciliary, lonDomiciliary);
        MarkerOptions b = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_domiciliary));
        b.position(yourPo);

        if (g == 0) {
            //DESDE aqui se hace seguimiento de camara al domiciliario
            m2 = mMap.addMarker(b);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(yourPo));
            CameraPosition camPosi = new CameraPosition.Builder()
                    .target(yourPo)   //Centramos el mapa
                    .zoom(13)        //Establecemos el zoom
                    .build();
            CameraUpdate camUpda3 = CameraUpdateFactory.newCameraPosition(camPosi);
            mMap.animateCamera(camUpda3);
            g = 1;
            validateDomiciliaryRealtime = true;
            //HASTA aqui se hace seguimiento de camara al domiciliario
        }
        m2.setPosition(yourPo);
        if (!validateDomiciliaryRealtime) {
            m2.remove();
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
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));
//            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
//            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
//            Log.w("jjj", "Duration Seconds-->> "+route.duration.value);
//            Log.w("jjj", "Distance Meters-->> "+route.distance.value);
//            Log.w("jjj", "Duration-->> "+route.duration.text);
//            Log.w("jjj", "Distance-->> "+route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_from))
                    .title(getResources().getString(R.string.text_marker_from))
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_to))
                    .title(getResources().getString(R.string.text_marker_to))
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorLineRoute)))).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenForUpdate();
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