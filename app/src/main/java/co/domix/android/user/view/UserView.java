package co.domix.android.user.view;

/**
 * Created by unicorn on 11/11/2017.
 */

public interface UserView {
    void startGetLocation();
    void responseForFullnameAndPhone(boolean result);
    void alertNoGps();
    void openDialogSendContactData();
    void sendContactData(String firstName, String lastName, String phone);
    void resultErrorRequest();
    void contactDataSent();
    void goPickMap();
    void responseFromName(String from);
    void responseToName(String to);
    void responseEmptyFields(String toastMessage);
    void responseCash(int priceInCash, String countryO, String countryOrigen, String cityOrigen,
                      int distanceBetweenPoints, int myCredit);
    void responseSuccessRequest(int getCountFull);

    void showNotInternet();
    void showYesInternet();
    void showProgressBar();
    void hideProgressBar();
}
