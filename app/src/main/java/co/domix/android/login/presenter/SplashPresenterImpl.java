package co.domix.android.login.presenter;

import com.google.firebase.auth.FirebaseUser;

import co.domix.android.login.interactor.SplashInteractor;
import co.domix.android.login.interactor.SplashInteractorImpl;
import co.domix.android.login.view.Splash;
import co.domix.android.login.view.SplashView;

/**
 * Created by unicorn on 11/22/2017.
 */

public class SplashPresenterImpl implements SplashPresenter {

    private SplashView view;
    private SplashInteractor interactor;


    public SplashPresenterImpl(SplashView view) {
        this.view = view;
        interactor = new SplashInteractorImpl(this);
    }

    @Override
    public void queryStatePosition(String uid, Splash splash) {
        interactor.queryStatePosition(uid, splash);
    }

    @Override
    public void verifyNetworkAndInternet(Splash splash, boolean isOnline, FirebaseUser firebaseUser, String uid) {
        interactor.verifyNetworkAndInternet(splash, isOnline, firebaseUser, uid);
    }

    @Override
    public void startGetLocation() {
        view.startGetLocation();
    }

    @Override
    public void goOrderCatched(int idOrder) {
        view.goOrderCatched(idOrder);
    }

    @Override
    public void goOrderRequested(int idOrder) {
        view.goOrderRequested(idOrder);
    }

    @Override
    public void goUserScore(int idOrder) {
        view.goUserScore(idOrder);
    }

    @Override
    public void goDomiciliaryScore(int idOrder) {
        view.goDomiciliaryScore(idOrder);
    }

    @Override
    public void goHome() {
        view.goHome();
    }

    @Override
    public void goLogin() {
        view.goLogin();
    }

    @Override
    public void executeAlertNoNetwork() {
        view.alertNoNetwork();
    }

    @Override
    public void notInternetConnection() {
        view.notInternetConnection();
    }
}
