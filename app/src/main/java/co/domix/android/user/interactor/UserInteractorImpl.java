package co.domix.android.user.interactor;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.directionModule.DirectionFinder;
import co.domix.android.directionModule.DirectionFinderListener;
import co.domix.android.directionModule.Route;
import co.domix.android.user.presenter.UserPresenter;
import co.domix.android.user.repository.UserRepository;
import co.domix.android.user.repository.UserRepositoryImpl;

/**
 * Created by unicorn on 11/12/2017.
 */

public class UserInteractorImpl implements UserInteractor, DirectionFinderListener {

    private LocationManager locationManager;
    private DomixApplication app;
    private int minFare, priceInCash, priceInEcoin;
    private double fareToApply;
    private String coordsFromPrice, coordsToPrice, cityOrigen, countryOrigen, countryO;
    private List<Address> geocodeMatches = null;
    private UserPresenter presenter;
    private UserRepository repository;

    public UserInteractorImpl(UserPresenter presenter) {
        this.presenter = presenter;
        repository = new UserRepositoryImpl(presenter, this);
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
    public void requestForFullnameAndPhone(String uid) {
        repository.requestForFullnameAndPhone(uid);
    }

    @Override
    public void requestGeolocationAndDistance(String latFrom, String lonFrom, String latTo, String lonTo, int whatAddress, Activity activity) {
        if (whatAddress == 0){
            if (!latFrom.equals(null) || !lonFrom.equals(null)){
                String arr [] = getGeolocation(latFrom, lonFrom, activity);
                coordsFromPrice = arr[3];
                presenter.responseFromName(arr[2]);
            }
        } else if (whatAddress == 1){
            if (!latTo.equals(null) || !lonTo.equals(null)){
                String arr [] = getGeolocation(latTo, lonTo, activity);
                coordsToPrice = arr[3];
                countryOrigen = arr[0];
                cityOrigen = arr[1];
                repository.requestFare(arr[0]);
                presenter.responseToName(arr[2]);
            }
        }

        if (coordsFromPrice != null && coordsToPrice != null) {
            try {
                new DirectionFinder(this, coordsFromPrice, coordsToPrice).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public String [] getGeolocation(String latFrom, String lonFrom, Activity activity){
        String arr [] = new String[4];
        double latitFrom = Double.valueOf(latFrom);
        double longiFrom = Double.valueOf(lonFrom);
        try {
            geocodeMatches = new Geocoder(activity).getFromLocation(latitFrom, longiFrom, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!geocodeMatches.isEmpty()) {
            arr[0] = geocodeMatches.get(0).getCountryCode();
            arr[1] = geocodeMatches.get(0).getLocality();
            arr[2] = geocodeMatches.get(0).getFeatureName();
        }
        arr[3] = latFrom + ", " + lonFrom;
        return arr;
    }

    @Override
    public void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity) {
        repository.sendContactData(uid, firstName, lastName, phone, activity);
    }

    @Override
    public void request(boolean fieldsWasFill, String uid, String email, String country, String city,
                        String from, String to, String description1, String description2, byte dimenSelected,
                        byte payMethod, int paymentCash, Activity activity) {
        if (from.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_indicate_starting_point));
        } else if (to.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_specify_destination_point));
        } else if (description1.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_enter_description1));
        } else if (description2.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_enter_description2));
        } else if (String.valueOf(paymentCash).equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_enter_payment_amount));
        } else {
            if (fieldsWasFill) {
                repository.request(uid, email, country, city, from, to, description1, description2,
                                    dimenSelected, payMethod, paymentCash, activity);
            } else {
                presenter.openDialogSendContactData();
            }
        }
    }

    @Override
    public void responseFare(double fare) {
        fareToApply = fare;
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            double priceDouble = route.distance.value * fareToApply;
            String dos = String.format("%.1f", priceDouble / 1000);
            double tres = Double.valueOf(dos);
            priceInCash = (int) (tres * 1000);
            if (countryOrigen.equals("CO")) {
                countryO = "COP";
                minFare = 3500;
            } else if (countryOrigen == "CL") {
                countryO = "CLP";
                minFare = 740;
            }
            if (priceInCash < minFare) {
                priceInCash = minFare;
            }
            priceInEcoin = ((priceInCash / 174) * 2) * 100;
            presenter.responseCash(priceInCash, countryO, countryOrigen, cityOrigen, priceInEcoin);
        }
    }
}
