package co.domix.android.domiciliary.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.model.Coordinate;
import co.domix.android.utils.ToastsKt;

/**
 * Created by unicorn on 2/4/2018.
 */

public class CoordinateService extends Service implements LocationListener, GpsStatus.Listener {

    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private DomixApplication app;
    private LocationManager mLocationManager;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceCoord = database.getReference("coordinate");

    public void onCreate() {
        super.onCreate();
        app = (DomixApplication) getApplicationContext();
        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationManager != null) {
            mLocationManager.addGpsStatusListener(this);
        }
        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    500,
                    0,
                    this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            Log.w("jjj", "Lat-> "+location.getLatitude());
            Log.w("jjj", "Lon-> "+location.getLongitude());
            editor.putString(getString(R.string.const_sharedPref_key_lat_device), String.valueOf(location.getLatitude()));
            editor.putString(getString(R.string.const_sharedPref_key_lon_device), String.valueOf(location.getLongitude()));
            editor.commit();
            sendCoordinates();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onGpsStatusChanged(int i) {

    }

    public void onDestroy(){
        super.onDestroy();
        stopSelf();
    }

    public void sendCoordinates() {
        referenceCoord.child(app.uId).child("latitude").setValue(shaPref.getString(getString(R.string.const_sharedPref_key_lat_device), ""));
        referenceCoord.child(app.uId).child("longitude").setValue(shaPref.getString(getString(R.string.const_sharedPref_key_lon_device), ""));
    }


}
