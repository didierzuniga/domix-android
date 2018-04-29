package co.domix.android.home.interactor;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;

import co.domix.android.DomixApplication;
import co.domix.android.home.presenter.HomePresenter;
import co.domix.android.home.repository.HomeRepository;
import co.domix.android.home.repository.HomeRepositoryImpl;

/**
 * Created by unicorn on 11/14/2017.
 */

public class HomeInteractorImpl implements HomeInteractor {

    private DomixApplication app;
    private LocationManager locationManager;
    private HomePresenter presenter;
    private HomeRepository repository;

    public HomeInteractorImpl(HomePresenter presenter) {
        this.presenter = presenter;
        repository = new HomeRepositoryImpl(presenter);
    }

    @Override
    public void verifyLocationAndInternet(Activity activity) {
        app = (DomixApplication) activity.getApplicationContext();
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!network_enabled) {
            presenter.alertNoGps();
        } else {
            if (!app.isOnline()) {
                presenter.showNotInternet();
            } else {
                presenter.startGetLocation();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                presenter.hideProgressBar();
                            }
                        }, 300);
                    }
                });
                presenter.showYesInternet();
            }
        }
    }
}