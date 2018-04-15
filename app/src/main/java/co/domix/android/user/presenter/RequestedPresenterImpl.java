package co.domix.android.user.presenter;

import co.domix.android.user.interactor.RequestedInteractor;
import co.domix.android.user.interactor.RequestedInteractorImpl;
import co.domix.android.user.view.Requested;
import co.domix.android.user.view.RequestedView;

/**
 * Created by unicorn on 11/12/2017.
 */

public class RequestedPresenterImpl implements RequestedPresenter {

    private RequestedView view;
    private RequestedInteractor interactor;

    public RequestedPresenterImpl(RequestedView view) {
        this.view = view;
        interactor = new RequestedInteractorImpl(this);
    }

    @Override
    public void listenForUpdate(int idOrder, Requested requested) {
        interactor.listenForUpdate(idOrder, requested);
    }

    @Override
    public void responseDomiciliaryCatched(String id, String rate, String name, String cellPhone, int usedVehicle) {
        view.responseDomiciliaryCatched(id, rate, name, cellPhone, usedVehicle);
    }

    @Override
    public void dialogCancel(int idOrder, Requested requested) {
        interactor.dialogCancel(idOrder, requested);
    }

    @Override
    public void resultGoUserActivity() {
        view.resultGoUserActivity();
    }

    @Override
    public void goRateUser() {
        view.goRateUser();
    }

    @Override
    public void responseCoordinatesFromTo(String oriLa, String oriLo, String desLa, String desLo) {
        view.resultCoordinatesFromTo(oriLa, oriLo, desLa, desLo);
    }

    @Override
    public void updateDomiPosition(int idOrder, Requested requested) {
        interactor.updateDomiPosition(idOrder, requested);
    }

    @Override
    public void responseCoordDomiciliary(double latDomiciliary, double lonDomiciliary) {
        view.responseCoordDomiciliary(latDomiciliary, lonDomiciliary);
    }

    @Override
    public void resultNotCatched() {
        view.resultNotCatched();
    }
}
