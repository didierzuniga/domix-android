package co.domix.android.domiciliary.presenter;

import co.domix.android.domiciliary.interactor.DomiciliaryInteractor;
import co.domix.android.domiciliary.interactor.DomiciliaryInteractorImpl;
import co.domix.android.domiciliary.view.Domiciliary;
import co.domix.android.domiciliary.view.DomiciliaryView;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryPresenterImpl implements DomiciliaryPresenter {

    private DomiciliaryView view;
    private DomiciliaryInteractor interactor;

    public DomiciliaryPresenterImpl(DomiciliaryView view) {
        this.view = view;
        interactor = new DomiciliaryInteractorImpl(this);
    }

    @Override
    public void searchDeliveries() {
        interactor.searchDeliveries();
    }

    @Override
    public void sendDataDomiciliary(Domiciliary domiciliary, int idOrderToSend, String uid) {
        interactor.sendDataDomiciliary(domiciliary, idOrderToSend, uid);
    }

    @Override
    public void goCompareDistance(int idOrder, String ago, String from, String to, String description1,
                                  String description2, String oriLat, String oriLon, String desLat,
                                  String desLon) {
        view.goCompareDistance(idOrder, ago, from, to, description1, description2, oriLat, oriLon, desLat, desLon);
    }

    @Override
    public void countChild(int countChild) {
        view.countChild(countChild);
    }

    @Override
    public void responseOrderHasBeenTaken() {
        view.responseOrderHasBeenTaken();
    }

    @Override
    public void responseGoOrderCatched(String idOrder) {
        view.responseGoOrderCatched(idOrder);
    }

    @Override
    public void queryForFullnameAndPhone(String uid) {
        interactor.queryForFullnameAndPhone(uid);
    }

    @Override
    public void sendContactData(String uid, String firstName, String lastName, String phone, Domiciliary domiciliary) {
        interactor.sendContactData(uid, firstName, lastName, phone, domiciliary);
    }

    @Override
    public void contactDataSent() {
        view.contactDataSent();
    }

    @Override
    public void queryUserRate(String idOrder) {
        interactor.queryUserRate(idOrder);
    }

    @Override
    public void responseQueryRate(String rate) {
        view.responseQueryRate(rate);
    }

    @Override
    public void responseForFullnameAndPhone(boolean result) {
        view.responseForFullnameAndPhone(result);
    }


}
