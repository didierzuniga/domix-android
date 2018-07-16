package co.domix.android.domiciliary.repository;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryRepository {
    void searchDeliveries(String lat, String lon);
    void sendDataDomiciliary(Activity activity, int idOrderToSend, String uid, int transportUsed, String country);
    void queryPersonalDataFill(String uid);
    void queryUserRate(String idOrder);
}
