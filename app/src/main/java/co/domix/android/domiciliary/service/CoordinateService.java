package co.domix.android.domiciliary.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import co.domix.android.DomixApplication;

/**
 * Created by unicorn on 2/4/2018.
 */

public class CoordinateService extends Service {

    private String latitude;
    private String longitude;
    private Timer timer;
    private boolean runningTimer;
    private LocationManager mLocationManager = null;
    private DomixApplication app;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceCoord = database.getReference("coordinate");

    public class LocationListener implements android.location.LocationListener{

        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            if (!runningTimer){
                runningTimer = true;
                timer.schedule(new sendCoordinates(), 5000, 5000);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.w("jjj", "onStatusChanged: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Log.w("jjj", "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Log.w("jjj", "onProviderDisabled: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onCreate(){
        super.onCreate();
        timer = new Timer();
        app = (DomixApplication) getApplicationContext();
        runningTimer = false;
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 7000, 10f,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            //Log.w("jjj", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.w("jjj", "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 7000, 10f,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            //Log.w("jjj", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.w("jjj", "gps provider does not exist " + ex.getMessage());
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    //Log.w("jjj", "fail to remove location listners, ignore", ex);
                }
            }
        }
        timer.cancel();
        stopSelf();
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class sendCoordinates extends TimerTask {
        @Override
        public void run() {
            referenceCoord.child(app.uId).child("latitude").setValue(latitude);
            referenceCoord.child(app.uId).child("longitude").setValue(longitude);
            runningTimer = true;
        }
    }

}
