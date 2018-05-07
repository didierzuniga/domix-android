package co.domix.android.domiciliary.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import co.domix.android.model.Coordinate;
import co.domix.android.utils.ToastsKt;

/**
 * Created by unicorn on 2/4/2018.
 */

public class CoordinateService extends Service {

    private LocationManager locManager;
    private Location loc;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    //    private String latitude;
//    private String longitude;
    private Timer timer;
    //    private boolean runningTimer;
//    private LocationManager mLocationManager = null;
    private DomixApplication app;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceCoord = database.getReference("coordinate");

//    public class LocationListener implements android.location.LocationListener{
//
//        Location mLastLocation;
//
//        public LocationListener(String provider) {
//            mLastLocation = new Location(provider);
//        }
//
//        @Override
//        public void onLocationChanged(Location location) {
//            mLastLocation.set(location);
//            latitude = String.valueOf(location.getLatitude());
//            longitude = String.valueOf(location.getLongitude());
//            if (!runningTimer){
//                runningTimer = true;
//                timer.schedule(new sendCoordinates(), 5000, 5000);
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            //Log.w("jjj", "onStatusChanged: " + provider);
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            //Log.w("jjj", "onProviderEnabled: " + provider);
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            //Log.w("jjj", "onProviderDisabled: " + provider);
//        }
//    }

//    LocationListener[] mLocationListeners = new LocationListener[] {
//            new LocationListener(LocationManager.GPS_PROVIDER),
//            new LocationListener(LocationManager.NETWORK_PROVIDER)
//    };

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

    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        app = (DomixApplication) getApplicationContext();

        shaPref = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = shaPref.edit();

        timer.schedule(new inicializar(), 5000, 5000);

//        runningTimer = false;
//        initializeLocationManager();
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, 7000, 10f,
//                    mLocationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//            //Log.w("jjj", "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            //Log.w("jjj", "network provider does not exist, " + ex.getMessage());
//        }
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, 7000, 10f,
//                    mLocationListeners[0]);
//        } catch (java.lang.SecurityException ex) {
//            //Log.w("jjj", "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            //Log.w("jjj", "gps provider does not exist " + ex.getMessage());
//        }
    }

    private class inicializar extends TimerTask {
        @Override
        public void run() {
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(CoordinateService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CoordinateService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.w("jjj", "CoordinateService Location: "+loc.getLongitude());
            if (loc != null){
                Log.w("jjj", "CoordinateService - leo coordenadas antes de enviar a DB");
                editor.putString("latitude", String.valueOf(loc.getLatitude()));
                editor.putString("longitude",String.valueOf(loc.getLongitude()));
                editor.commit();
                sendCoordinates();
            }
        }
    }

    public void onDestroy(){
        super.onDestroy();
//        if (mLocationManager != null) {
//            for (int i = 0; i < mLocationListeners.length; i++) {
//                try {
//                    mLocationManager.removeUpdates(mLocationListeners[i]);
//                } catch (Exception ex) {
//                    //Log.w("jjj", "fail to remove location listners, ignore", ex);
//                }
//            }
//        }
        timer.cancel();
        stopSelf();
    }

//    private void initializeLocationManager() {
//        if (mLocationManager == null) {
//            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        }
//    }

//    private class sendCoordinates extends TimerTask {
//        @Override
//        public void run() {
//            referenceCoord.child(app.uId).child("latitude").setValue(latitude);
//            referenceCoord.child(app.uId).child("longitude").setValue(longitude);
//            runningTimer = true;
//        }
//    }

    public void sendCoordinates() {
        Log.w("jjj", "CoordinateService - envio coordenadas a DB");
        referenceCoord.child(app.uId).child("latitude").setValue(shaPref.getString("latitude", ""));
        referenceCoord.child(app.uId).child("longitude").setValue(shaPref.getString("longitude", ""));
//        timer.schedule(new inicializar(), 5000, 5000);
    }


}
