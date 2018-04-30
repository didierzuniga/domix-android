package co.domix.android.login.presenter;

import com.google.firebase.auth.FirebaseUser;

import co.domix.android.login.view.Splash;

/**
 * Created by unicorn on 11/22/2017.
 */

public interface SplashPresenter {
    void queryStatePosition(String uid, Splash splash);
    void verifyNetworkAndInternet(Splash splash, boolean isOnline, FirebaseUser firebaseUser, String uid);
    void startGetLocation();
    void goOrderCatched(int idOrder);
    void goOrderRequested(int idOrder);
    void goUserScore(int idOrder);
    void goDomiciliaryScore(int idOrder);
    void goHome();
    void goLogin();
    void executeAlertNoNetwork();
    void notInternetConnection();
}
