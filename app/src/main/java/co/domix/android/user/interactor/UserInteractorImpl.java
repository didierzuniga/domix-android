package co.domix.android.user.interactor;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
    private boolean countryAvailable = false;
    private int minFare, priceInCash, myCredit, routeInt;
    private float fareToApply, routeDouble;
    private String coordsFromPrice, coordsToPrice, cityOrigen, countryOrigen, currencyCode, userID,
                   countryFrom, countryTo;
    private List<Address> geocodeMatches = null;
    private List<String> countriesOk;
    private UserPresenter presenter;
    private UserRepository repository;

    public UserInteractorImpl(UserPresenter presenter) {
        this.presenter = presenter;
        repository = new UserRepositoryImpl(presenter, this);
    }

    @Override
    public void queryPersonalDataFill(String uid) {
        repository.queryPersonalDataFill(uid);
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
    public void requestGeolocationAndDistance(String uid, String latFrom, String lonFrom, String latTo,
                                              String lonTo, int whatAddress, Activity activity) {
        userID = uid;
        if (whatAddress == 0){
            if (!latFrom.equals("") || !lonFrom.equals("")){
                String arr [] = getGeolocation(latFrom, lonFrom, activity);
                coordsFromPrice = arr[3];
                countryFrom = arr[0];
                presenter.responseFromName(arr[2]);
            }
        } else if (whatAddress == 1){
            if (!latTo.equals("") || !lonTo.equals("")){
                String arr [] = getGeolocation(latTo, lonTo, activity);
                coordsToPrice = arr[3];
                countryTo = arr[0];
                countryOrigen = arr[0];
                cityOrigen = arr[1];
                presenter.responseToName(arr[2]);
            }
        }

        if (coordsFromPrice != null && coordsToPrice != null) {
            for (int i = 0; i < countriesOk.size(); i++ ){
                if (countryFrom.equals(countriesOk.get(i)) && countryTo.equals(countriesOk.get(i))){
                    countryAvailable = true;
                    try {
                        new DirectionFinder(this, coordsFromPrice, coordsToPrice).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            if (!countryAvailable){
                presenter.countryNotAvailable();
            }
        }
    }

    @Override
    public void responseForCountriesAvailable(List<String> countries) {
        countriesOk = new ArrayList<String>();
        countriesOk = countries;
    }

    @Override
    public void countriesAvailable() {
        repository.countriesAvailable();
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
    public void request(boolean fieldsWasFill, String uid, String email, String country, String city,
                        String from, String to, int disBetweenPoints, String description1, String description2, byte dimenSelected,
                        byte payMethod, int paymentCash, int creditUsed, int updateCredit, Activity activity) {
        if (from.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_indicate_starting_point));
        } else if (to.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_specify_destination_point));
        } else if (description1.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_enter_description1));
        } else if (description2.equals("")) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_enter_description2));
        } else if (paymentCash == 0 && creditUsed == 0) {
            presenter.responseEmptyFields(activity.getString(R.string.toast_country_not_available));
        } else {
            if (fieldsWasFill) {
                repository.request(uid, email, country, city, from, to, disBetweenPoints, description1, description2,
                                    dimenSelected, payMethod, paymentCash, creditUsed, updateCredit, activity);
            } else {
                presenter.messageDataNotFill(fieldsWasFill);
            }
        }
    }

    @Override
    public void responseFareAndMyCredit(String currency, float fare, int minFareCost, int credit) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        formatter.setMinimumFractionDigits(1);
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        currencyCode = currency;
        fareToApply = fare;
        minFare = minFareCost;
        myCredit = credit;
        routeDouble = routeInt * fareToApply;
        Float dos = new Float(formatter.format(routeDouble / 1000));
        priceInCash = (int) (dos * 1000);
        if (priceInCash < minFare) {
            priceInCash = minFare;
        }
        presenter.responseCash(priceInCash, currencyCode, countryOrigen, cityOrigen, routeInt, myCredit);

    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            routeInt = route.distance.value;
            repository.requestFareAndMyCredit(countryOrigen, userID);
        }
    }
}
