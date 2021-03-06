package co.domix.android.domiciliary.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryInteractor {
    void verifyLocationAndInternet(Activity activity);
    void searchDeliveries(String lat, String lon, int vehSelected);
    void goCompareDistance(int idOrder, String ago, String country, String from, String to, int sizeOrder,
                           String description1, String description2, String origenCoordinate,
                           String destineCoordinate, String latDomi, String lonDomi, int distanceBetween,
                           int minDistanceBetweenRequiredForCyclist, int minDistanceBetweenRequiredForOther);
    void countChild(int countChild);
    void sendDataDomiciliary(Activity activity, int idOrderToSend, String uid, int transportUsed, String country);
    void queryPersonalDataFill(String uid);
    void queryUserRate(String idOrder);
}
