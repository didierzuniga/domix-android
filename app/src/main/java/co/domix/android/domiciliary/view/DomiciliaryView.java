package co.domix.android.domiciliary.view;

import java.util.Hashtable;
import java.util.List;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface DomiciliaryView {
    void alertNoGps();
    void showYesInternet();
    void showNotInternet();
    void showProgressBar();
    void hideProgressBar();
    void searchDeliveries();
    void showResultOrder(Hashtable<Integer, List> dictionary, int countIndex);
    void showResultNotOrder();
    void goPreviewRouteOrder();
    void responseOrderHasBeenTaken();
    void responseGoOrderCatched(String idOrder);
    void queryPersonalDataFill();
    void queryUserRate(String idOrder);
    void responseQueryRate(String rate);
    void responseQueryPersonalDataFill(boolean fillData);
}
