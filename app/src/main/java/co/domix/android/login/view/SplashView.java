package co.domix.android.login.view;

/**
 * Created by unicorn on 11/9/2017.
 */

public interface SplashView {
    void showProgressBar();
    void hideProgressBar();
    void startGetLocation();
    void queryStatePosition(String uid);
    void goOrderCatched(int idOrder);
    void goOrderRequested(int idOrder);
    void goUserScore(int idOrder);
    void goDomiciliaryScore(int idOrder);
    void goHome();
    void goLogin();
    void alertNoNetwork();
    void notInternetConnection();
}
