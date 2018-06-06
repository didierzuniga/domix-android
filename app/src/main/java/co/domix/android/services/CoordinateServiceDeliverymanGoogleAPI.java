package co.domix.android.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.view.OrderCatched;

public class CoordinateServiceDeliverymanGoogleAPI extends Service implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient apiClient;
    private LocationManager locationManager;
    private LocationRequest locRequest;
    private Timer timer;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private DomixApplication app;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceCoord = database.getReference("coordinate");

    public void onCreate() {
        super.onCreate();
        app = (DomixApplication) getApplicationContext();
        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();

        timer = new Timer();
        timer.schedule(new updateCoordinates(), 1000, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class updateCoordinates extends TimerTask {
        @Override
        public void run() {
            apiClient = new GoogleApiClient.Builder(CoordinateServiceDeliverymanGoogleAPI.this)
                    .addConnectionCallbacks(CoordinateServiceDeliverymanGoogleAPI.this)
                    .addOnConnectionFailedListener(CoordinateServiceDeliverymanGoogleAPI.this)
                    .addApi(LocationServices.API)
                    .build();
            apiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        Location lastLocation =
                LocationServices.FusedLocationApi.getLastLocation(apiClient);
        updateUI(lastLocation);
//        if (ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    101);
//        } else {
//            Location lastLocation =
//                    LocationServices.FusedLocationApi.getLastLocation(apiClient);
//            updateUI(lastLocation);
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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
            Log.w("jjj", "Service GoogleAPI Latitu-> "+loc.getLatitude());
            editor.putString(getString(R.string.const_sharedPref_key_lat_device), String.valueOf(loc.getLatitude()));
            editor.putString(getString(R.string.const_sharedPref_key_lon_device), String.valueOf(loc.getLongitude()));
            editor.commit();
            referenceCoord.child(app.uId).child("latitude").setValue(shaPref.getString(getString(R.string.const_sharedPref_key_lat_device), ""));
            referenceCoord.child(app.uId).child("longitude").setValue(shaPref.getString(getString(R.string.const_sharedPref_key_lon_device), ""));
        }
    }

    public void onDestroy(){
        super.onDestroy();
        timer.cancel();
        stopSelf();
    }
}
