package co.domix.android.user.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import co.domix.android.R;

public class PickMap extends AppCompatActivity implements OnMapReadyCallback {

    private String latit, longi;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private FloatingActionButton fabOk;
    private FloatingActionButton fabFail;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();

        fabOk = (FloatingActionButton) findViewById(R.id.fabOk);
        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whatAdd = shaPref.getInt("whatAddress", 2);
                if (whatAdd == 0) {
                    editor.putString("latFrom", latit);
                    editor.putString("lonFrom", longi);
                    editor.commit();
                } else if (whatAdd == 1) {
                    editor.putString("latTo", latit);
                    editor.putString("lonTo", longi);
                    editor.commit();
                }
                onBackPressed();
            }
        });

        fabFail = (FloatingActionButton) findViewById(R.id.fabFail);
        fabFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat = Double.valueOf(shaPref.getString(getString(R.string.const_sharedPref_key_lat_device), ""));
        double lon = Double.valueOf(shaPref.getString(getString(R.string.const_sharedPref_key_lon_device), ""));
        LatLng myPos = new LatLng(lat, lon);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                latit = String.valueOf(cameraPosition.target.latitude);
                longi = String.valueOf(cameraPosition.target.longitude);
            }
        });
    }
}
