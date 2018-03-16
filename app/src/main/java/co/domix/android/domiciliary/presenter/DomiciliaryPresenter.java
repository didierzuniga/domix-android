package co.domix.android.domiciliary.presenter;

import co.domix.android.domiciliary.view.Domiciliary;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryPresenter {
    void searchDeliveries();
    void sendDataDomiciliary(Domiciliary domiciliary, int idOrderToSend, String uid);
    void goCompareDistance(int idOrder, String ago, String from, String to, String description1,
                           String description2, String oriLat, String oriLon, String desLat,
                           String desLon);
    void countChild(int countChild);
    void responseOrderHasBeenTaken();
    void responseGoOrderCatched(String idOrder);
    void queryForFullnameAndPhone(String uid);
    void sendContactData(String uid, String firstName, String lastName, String phone, Domiciliary domiciliary);
    void contactDataSent();
    void queryUserRate(String idOrder);
    void responseQueryRate(String rate);
    void responseForFullnameAndPhone(boolean result);
}
