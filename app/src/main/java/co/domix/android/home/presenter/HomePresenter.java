package co.domix.android.home.presenter;

import co.domix.android.home.view.Home;

/**
 * Created by unicorn on 11/11/2017.
 */

public interface HomePresenter {
    void verifyLocationAndInternet(Home home);
    void alertNoGps();
    void showNotInternet();
    void showYesInternet();
    void hideProgressBar();
}
