package co.domix.android.domiciliary.interactor;

import android.app.Activity;

import co.domix.android.domiciliary.presenter.DomiciliaryPresenter;
import co.domix.android.domiciliary.repository.DomiciliaryRepository;
import co.domix.android.domiciliary.repository.DomiciliaryRepositoryImpl;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryInteractorImpl implements DomiciliaryInteractor {

    private DomiciliaryPresenter presenter;
    private DomiciliaryRepository repository;

    public DomiciliaryInteractorImpl(DomiciliaryPresenter presenter) {
        this.presenter = presenter;
        repository = new DomiciliaryRepositoryImpl(presenter);
    }


    @Override
    public void searchDeliveries() {
        repository.searchDeliveries();
    }

    @Override
    public void sendDataDomiciliary(Activity activity, int idOrderToSend, String uid) {
        repository.sendDataDomiciliary(activity, idOrderToSend, uid);
    }

    @Override
    public void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity) {
        repository.sendContactData(uid, firstName, lastName, phone);
    }

    @Override
    public void queryForFullnameAndPhone(String uid) {
        repository.queryForFullnameAndPhone(uid);
    }

    @Override
    public void queryUserRate(String idOrder) {
        repository.queryUserRate(idOrder);
    }
}
