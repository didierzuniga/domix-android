package co.domix.android.user.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface UserInteractor {
    void verifyLocationAndInternet(Activity activity);
    void requestForFullnameAndPhone(String uid);
    void requestGeolocationAndDistance(String latFrom, String lonFrom, String latTo, String lonTo, int whatAddress, Activity activity);
    void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity);
    void request(boolean fieldsWasFill, String uid, String email, String country, String city,
                 String from, String to, int disBetweenPoints, String description1, String description2, byte dimenSelected,
                 byte payMethod, int paymentCash, Activity activity);
    void responseFare(double fare);
}
