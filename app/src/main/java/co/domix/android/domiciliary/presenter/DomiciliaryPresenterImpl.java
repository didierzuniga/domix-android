package co.domix.android.domiciliary.presenter;

import java.util.Hashtable;
import java.util.List;

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
    public void alertNoGps() {
        view.alertNoGps();
    }

    @Override
    public void showYesInternet() {
        view.showYesInternet();
    }

    @Override
    public void showNotInternet() {
        view.showNotInternet();
    }

    @Override
    public void hideProgressBar() {
        view.hideProgressBar();
    }

    @Override
    public void verifyLocationAndInternet(Domiciliary domiciliary) {
        interactor.verifyLocationAndInternet(domiciliary);
    }

    @Override
    public void searchDeliveries(String lat, String lon, int vehSelected) {
        interactor.searchDeliveries(lat, lon, vehSelected);
    }

    @Override
    public void sendDataDomiciliary(Domiciliary domiciliary, int idOrderToSend, String uid, int transportUsed,
                                    String country) {
        interactor.sendDataDomiciliary(domiciliary, idOrderToSend, uid, transportUsed, country);
    }

//    @Override
//    public void goCompareDistance(int idOrder, String ago, String from, String to, String description1,
//                                  String description2, String oriLat, String oriLon, String desLat,
//                                  String desLon) {
//        view.goCompareDistance(idOrder, ago, from, to, description1, description2, oriLat, oriLon, desLat, desLon);
//    }

//    @Override
//    public void countChild(int countChild) {
//        view.countChild(countChild);
//    }

    @Override
    public void showResultOrder(Hashtable<Integer, List> dictionary, int countIndex) {
        view.showResultOrder(dictionary, countIndex);
    }

    @Override
    public void showResultNotOrder() {
        view.showResultNotOrder();
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
    public void queryPersonalDataFill(String uid) {
        interactor.queryPersonalDataFill(uid);
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
    public void responseQueryPersonalDataFill(boolean fillData) {
        view.responseQueryPersonalDataFill(fillData);
    }


}
