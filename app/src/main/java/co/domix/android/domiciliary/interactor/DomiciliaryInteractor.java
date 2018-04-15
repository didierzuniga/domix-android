package co.domix.android.domiciliary.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryInteractor {
    void verifyLocationAndInternet(Activity activity);
    void searchDeliveries(String lat, String lon);
    void goCompareDistance(int idOrder, String ago, String from, String to, int sizeOrder, String description1,
                           String description2, String oriLat, String oriLon, String desLat,
                           String desLon, String latDomi, String lonDomi);
    void countChild(int countChild);
    void sendDataDomiciliary(Activity activity, int idOrderToSend, String uid, int transportUsed);
    void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity);
    void queryForFullnameAndPhone(String uid);
    void queryUserRate(String idOrder);
}
