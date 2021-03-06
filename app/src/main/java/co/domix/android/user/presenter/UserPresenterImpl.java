package co.domix.android.user.presenter;

import co.domix.android.user.interactor.UserInteractor;
import co.domix.android.user.interactor.UserInteractorImpl;
import co.domix.android.user.view.User;
import co.domix.android.user.view.UserView;

/**
 * Created by unicorn on 11/12/2017.
 */

public class UserPresenterImpl implements UserPresenter {

    private UserView view;
    private UserInteractor interactor;

    public UserPresenterImpl(UserView view) {
        this.view = view;
        interactor = new UserInteractorImpl(this);
    }

    @Override
    public void queryPersonalDataFill(String uid) {
        interactor.queryPersonalDataFill(uid);
    }

    @Override
    public void responseQueryPersonalDataFill(boolean fillData) {
        view.responseQueryPersonalDataFill(fillData);
    }

    @Override
    public void messageDataNotFill(boolean showAlert) {
        view.messageDataNotFill(showAlert);
    }

    @Override
    public void verifyLocationAndInternet(User user) {
        interactor.verifyLocationAndInternet(user);
    }

    @Override
    public void alertNoGps() {
        view.alertNoGps();
    }

    @Override
    public void requestGeolocationAndDistance(String uid, String latFrom, String lonFrom, String latTo,
                                              String lonTo, int whatAddress, User user) {
        interactor.requestGeolocationAndDistance(uid, latFrom, lonFrom, latTo, lonTo, whatAddress, user);
    }

    @Override
    public void request(boolean fieldsWasFill, String uid, String email, String country, String city,
                        String from, String to, int disBetweenPoints, String description1, String description2, byte dimenSelected,
                        byte payMethod, int paymentCash, int creditUsed, int updateCredit, User user) {
        interactor.request(fieldsWasFill, uid, email, country, city, from, to, disBetweenPoints, description1, description2,
                dimenSelected, payMethod, paymentCash, creditUsed, updateCredit, user);
    }

    @Override
    public void responseSuccessRequest(int getCountFull) {
        view.responseSuccessRequest(getCountFull);
    }

    @Override
    public void responseFromName(String from) {
        view.responseFromName(from);
    }

    @Override
    public void responseToName(String to) {
        view.responseToName(to);
    }

    @Override
    public void responseEmptyFields(String toastMessage) {
        view.responseEmptyFields(toastMessage);
    }

    @Override
    public void responseCash(int priceInCash, String countryO, String countryOrigen, String cityOrigen,
                             int distanceBetweenPoints, int myCredit) {
        view.responseCash(priceInCash, countryO, countryOrigen, cityOrigen, distanceBetweenPoints, myCredit);
    }

    @Override
    public void resultErrorRequest() {
        view.resultErrorRequest();
    }

    @Override
    public void countryNotAvailable() {
        view.countryNotAvailable();
    }

    @Override
    public void countriesAvailable() {
        interactor.countriesAvailable();
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
    public void showProgressBar() {
        view.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        view.hideProgressBar();
    }
}
