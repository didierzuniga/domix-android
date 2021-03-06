package co.domix.android.user.view;

/**
 * Created by unicorn on 11/11/2017.
 */

public interface UserView {
    void responseQueryPersonalDataFill(boolean fillData);
    void messageDataNotFill(boolean showAlert);
    void alertNoGps();
    void resultErrorRequest();
    void goPickMap();
    void responseFromName(String from);
    void responseToName(String to);
    void responseEmptyFields(String toastMessage);
    void responseCash(int priceInCash, String countryO, String countryOrigen, String cityOrigen,
                      int distanceBetweenPoints, int myCredit);
    void responseSuccessRequest(int getCountFull);

    void countryNotAvailable();
    void showNotInternet();
    void showYesInternet();
    void showProgressBar();
    void hideProgressBar();
}
