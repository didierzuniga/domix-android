package co.domix.android.domiciliary.interactor;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import co.domix.android.DomixApplication;
import co.domix.android.directionModule.DirectionFinder;
import co.domix.android.directionModule.DirectionFinderListener;
import co.domix.android.directionModule.Route;
import co.domix.android.domiciliary.presenter.DomiciliaryPresenter;
import co.domix.android.domiciliary.repository.DomiciliaryRepository;
import co.domix.android.domiciliary.repository.DomiciliaryRepositoryImpl;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryInteractorImpl implements DomiciliaryInteractor, DirectionFinderListener {

    private LocationManager locationManager;
    private int countForDictionary, distMin, countIndex, countIndexTemp, countChilds, vehicleSelected;
    private List<String> listica;
    private Hashtable<Integer, List> diccionario;
    private List<Marker> originMarkers = new ArrayList<>(), destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private DomixApplication app;
    private DomiciliaryPresenter presenter;
    private DomiciliaryRepository repository;

    public DomiciliaryInteractorImpl(DomiciliaryPresenter presenter) {
        this.presenter = presenter;
        repository = new DomiciliaryRepositoryImpl(presenter, this);
    }


    @Override
    public void verifyLocationAndInternet(Activity activity) {
        app = (DomixApplication) activity.getApplicationContext();
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!network_enabled) {
            presenter.alertNoGps();
        } else {
            if (!app.isOnline()) {
                presenter.showNotInternet();
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                presenter.hideProgressBar();
                            }
                        }, 300);
                    }
                });
                presenter.showYesInternet();
            }
        }
    }

    @Override
    public void searchDeliveries(String lat, String lon, int vehSelected) {
        vehicleSelected = vehSelected; // Used in onDirectionFinderSuccess
        distMin = 0;
        countForDictionary = 0;
        countIndex = 0;
        countIndexTemp = 0;
        repository.searchDeliveries(lat, lon);
    }

    @Override
    public void goCompareDistance(int idOrder, String ago, String from, String to, int sizeOrder, String description1,
                                  String description2, String oriLat, String oriLon, String desLat,
                                  String desLon, String latDomi, String lonDomi) {
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
        listica.add(String.valueOf(sizeOrder));

        diccionario.put(countForDictionary, listica);

        try {
            String uno = oriLat + ", " + oriLon;
            String dos = latDomi + ", " + lonDomi;
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
    public void sendDataDomiciliary(Activity activity, int idOrderToSend, String uid, int transportUsed) {
        repository.sendDataDomiciliary(activity, idOrderToSend, uid, transportUsed);
    }

    @Override
    public void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity) {
        repository.sendContactData(uid, firstName, lastName, phone);
    }

    @Override
    public void queryForFullnameAndPhone(String uid) {
        repository.queryForFullnameAndPhone(uid);
    }

    @Override
    public void queryUserRate(String idOrder) {
        repository.queryUserRate(idOrder);
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
        for (Route route : routes) {

            // 4000 mts max
            int betweenStartEnd = 300; // Distance between start and end points
            if (vehicleSelected == 1){
                if (betweenStartEnd <= 4000 && route.distance.value <= 1500){
                    int newDistance = route.distance.value;
                    //Get distance between Deliveryman and start point
                    if (distMin != 0) {
                        if (distMin > newDistance) {
                            distMin = newDistance;
                            countIndex = countIndexTemp;
                        }
                    } else {
                        distMin = newDistance;
                    }
                }
            } else {
                int newDistance = route.distance.value;
                //Get distance between Deliveryman and start point
                if (distMin != 0) {
                    if (distMin > newDistance) {
                        distMin = newDistance;
                        countIndex = countIndexTemp;
                    }
                } else {
                    distMin = newDistance;
                }
            }
            countIndexTemp++;
        }

        if (countIndexTemp == countChilds) {
            presenter.showResultOrder(diccionario, countIndex);
        }
    }
}
