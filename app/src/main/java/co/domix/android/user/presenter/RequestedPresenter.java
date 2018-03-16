package co.domix.android.user.presenter;

import co.domix.android.user.view.Requested;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface RequestedPresenter {
    void listenForUpdate(int idOrder, Requested requested);
    void responseDomiciliaryCatched(String id, String rate, String name, String cellPhone);
    void dialogCancel(int idOrder, Requested requested);
    void resultGoUserActivity();
    void goRateUser();
    void responseCoordinatesFromTo(String oriLa, String oriLo, String desLa, String desLo);
    void updateDomiPosition(int idOrder, Requested requested);
    void responseCoordDomiciliary(double latDomiciliary, double lonDomiciliary);
    void resultNotCatched();
}
