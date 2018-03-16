package co.domix.android.home.view;

/**
 * Created by unicorn on 11/11/2017.
 */

public interface HomeView {
    void goUser();
    void goDomiciliary();
    void goProfile();
    void goHistory();
    void goSetting();
    void goEarnEcoin();
    void goPayment();
    void showProgressBar();
    void hideProgressBar();
    void alertNoGps();
    void showNotInternet();
    void showYesInternet();
    void logOut();
}
