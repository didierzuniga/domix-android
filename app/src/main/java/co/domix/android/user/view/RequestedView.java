package co.domix.android.user.view;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface RequestedView {
    void listenForUpdate();
    void responseDomiciliaryCatched(String id, String rate, String name, String cellPhone);
    void dialogCancel();
    void resultGoUserActivity();
    void goRateUser();
    void resultCoordinatesFromTo(String oriLa, String oriLo, String desLa, String desLo);
    void updateDomiPosition();
    void responseCoordDomiciliary(double latDomiciliary, double lonDomiciliary);
    void resultNotCatched();
    void domiLocated(double latDomiciliary, double lonDomiciliary);
}
