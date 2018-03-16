package co.domix.android.domiciliary.view;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface DomiciliaryView {
    void searchDeliveries();
    void goCompareDistance(int idOrder, String ago, String from, String to, String description1,
                           String description2, String oriLat, String oriLon, String desLat,
                           String desLon);
    void countChild(int countChild);
    void goPreviewRouteOrder();
    void sendDataDomiciliary();
    void responseOrderHasBeenTaken();
    void responseGoOrderCatched(String idOrder);
    void queryForFullnameAndPhone();
    void openDialogSendContactData();
    void sendContactData(String firstName, String lastName, String phone);
    void contactDataSent();
    void queryUserRate(String idOrder);
    void responseQueryRate(String rate);
    void responseForFullnameAndPhone(boolean result);
}
