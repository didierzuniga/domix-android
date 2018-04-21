package co.domix.android.user.repository;

import android.app.Activity;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface UserRepository {
    void requestForFullnameAndPhone(String uid);
    void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity);
    void request(String uid, String email, String country, String city, String from, String to,
                 int disBetweenPoints, String description1, String description2, byte dimenSelected, byte payMethod,
                 int paymentCash, Activity activity);
    void requestFare(String codeCountry);
}
