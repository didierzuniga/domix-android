package co.domix.android.home.presenter;

import co.domix.android.home.interactor.HomeInteractor;
import co.domix.android.home.interactor.HomeInteractorImpl;
import co.domix.android.home.view.Home;
import co.domix.android.home.view.HomeView;

/**
 * Created by unicorn on 11/11/2017.
 */

public class HomePresenterImpl implements HomePresenter {

    private HomeView view;
    private HomeInteractor interactor;

    public HomePresenterImpl(HomeView view) {
        this.view = view;
        interactor = new HomeInteractorImpl(this);
    }

    @Override
    public void verifyLocationAndInternet(Home home) {
        interactor.verifyLocationAndInternet(home);
    }

    @Override
    public void startGetLocation() {
        view.startGetLocation();
    }

    @Override
    public void alertNoGps() {
        view.alertNoGps();
    }

    @Override
    public void showNotInternet() {
        view.showNotInternet();
    }

    @Override
    public void showYesInternet() {
        view.showYesInternet();
    }

    @Override
    public void hideProgressBar() {
        view.hideProgressBar();
    }
}
