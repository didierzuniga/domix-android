package co.domix.android.domiciliary.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryInteractor {
    void searchDeliveries();
    void sendDataDomiciliary(Activity activity, int idOrderToSend, String uid);
    void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity);
    void queryForFullnameAndPhone(String uid);
    void queryUserRate(String idOrder);
}
