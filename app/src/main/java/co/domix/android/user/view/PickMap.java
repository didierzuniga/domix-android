package co.domix.android.user.view;

import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import co.domix.android.R;

public class PickMap extends AppCompatActivity implements OnMapReadyCallback {

    private String latit, longi;
    private SharedPreferences location;
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

        location = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = location.edit();

        fabOk = (FloatingActionButton) findViewById(R.id.fabOk);
        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whatAdd = location.getInt("whatAddress", 2);
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
        double lat = Double.valueOf(location.getString("latitude", ""));
        double lon = Double.valueOf(location.getString("longitude", ""));
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
