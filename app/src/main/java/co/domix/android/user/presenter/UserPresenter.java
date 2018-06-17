package co.domix.android.user.presenter;

import co.domix.android.user.view.User;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface UserPresenter {
    void queryPersonalDataFill(String uid);
    void responseQueryPersonalDataFill(boolean fillData);
    void messageDataNotFill(boolean showAlert);
    void verifyLocationAndInternet(User user);
    void alertNoGps();
    void requestGeolocationAndDistance(String uid, String latFrom, String lonFrom, String latTo, String lonTo, int whatAddress, User user);
    void request(boolean fieldsWasFill, String uid, String email, String country, String city,
                 String from, String to, int disBetweenPoints, String description1, String description2, byte dimenSelected,
                 byte payMethod, int paymentCash, int creditUsed, int updateCredit, User user);
    void responseSuccessRequest(int getCountFull);
    void responseFromName(String from);
    void responseToName(String to);
    void responseEmptyFields(String toastMessage);
    void responseCash(int priceInCash, String countryO, String countryOrigen, String cityOrigen,
                      int distanceBetweenPoints, int myCredit);
    void resultErrorRequest();
    void countryNotAvailable();
    void countriesAvailable();
    void showNotInternet();
    void showYesInternet();
    void showProgressBar();
    void hideProgressBar();
}
