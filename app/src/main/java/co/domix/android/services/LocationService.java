package co.domix.android.services;

import android.Manifest;
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
import android.util.Log;

/**
 * Created by unicorn on 4/29/2018.
 */

public class LocationService extends Service implements LocationListener, GpsStatus.Listener {

    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private LocationManager mLocationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        shaPref = getSharedPreferences("domx_prefs", MODE_PRIVATE);
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
                    LocationManager.GPS_PROVIDER,
                    500,
                    0,
                    this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
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
            Log.w("jjj", "Locations: "+location.getLongitude());
            editor.putString("latitude", String.valueOf(location.getLatitude()));
            editor.putString("longitude", String.valueOf(location.getLongitude()));
            editor.commit();
            stopSelf();
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

    /* Remove the locationlistener updates when Services is stopped */
    @Override
    public void onDestroy() {
        try {
            mLocationManager.removeUpdates(this);
            mLocationManager.removeGpsStatusListener(this);
            stopForeground(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGpsStatusChanged(int i) {

    }
}
