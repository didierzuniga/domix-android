package co.domix.android.user.interactor;

import android.app.Activity;

import java.util.List;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface UserInteractor {
    void queryPersonalDataFill(String uid);
    void verifyLocationAndInternet(Activity activity);
    void requestGeolocationAndDistance(String uid, String latFrom, String lonFrom, String latTo, String lonTo,
                                       int whatAddress, Activity activity);
    void request(boolean fieldsWasFill, String uid, String email, String country, String city,
                 String from, String to, int disBetweenPoints, String description1, String description2, byte dimenSelected,
                 byte payMethod, int paymentCash, int creditUsed, int updateCredit, Activity activity);
    void responseFareAndMyCredit(String currency, float fare, int minFareCost, int credit);
    void responseForCountriesAvailable(List<String> countries);
    void countriesAvailable();
}
