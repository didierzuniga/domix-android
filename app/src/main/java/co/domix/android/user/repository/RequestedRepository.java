package co.domix.android.user.repository;

import android.app.Activity;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface RequestedRepository {
    void listenForUpdate(int idOrder, Activity activity);
    void dialogCancel(int idOrder, Activity activity);
    void removeOrder(int idOrder, Activity activity);
    void deductCounters();
    void goRateUser();
    void updateDomiPosition(int idOrder, Activity activity);
    void requestCoordinates(String idDomiciliary, Activity activity);
    void getDataDomiciliary(String uidDomiciliary);
    void removeCoordDomiciliary(String uid);
}
