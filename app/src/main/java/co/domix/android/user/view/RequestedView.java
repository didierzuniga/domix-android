package co.domix.android.user.view;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface RequestedView {
    void listenForUpdate();
    void responseDomiciliaryCatched(String id, String rate, String name, String cellPhone, int usedVehicle);
    void dialogCancel(boolean afterTwoMinutes);
    void resultGoUserActivity();
    void goRateUser(int resultEarnedCredit, String currencyCode);
    void resultCoordinatesFromTo(String origenCoordinate, String destineCoordinate);
    void updateDomiPosition();
    void responseCoordDomiciliary(double latDomiciliary, double lonDomiciliary);
    void resultNotCatched();
    void domiLocated(double latDomiciliary, double lonDomiciliary);
}
