package co.domix.android.user.interactor;

import android.app.Activity;

import co.domix.android.user.presenter.RequestedPresenter;
import co.domix.android.user.repository.RequestedRepository;
import co.domix.android.user.repository.RequestedRepositoryImpl;

/**
 * Created by unicorn on 11/12/2017.
 */

public class RequestedInteractorImpl implements RequestedInteractor {

    private RequestedPresenter presenter;
    private RequestedRepository repository;


    public RequestedInteractorImpl(RequestedPresenter presenter) {
        this.presenter = presenter;
        repository = new RequestedRepositoryImpl(presenter);
    }

    @Override
    public void listenForUpdate(int idOrder, Activity activity) {
        repository.listenForUpdate(idOrder, activity);
    }

    @Override
    public void dialogCancel(String uid, int idOrder, Activity activity) {
        repository.dialogCancel(uid, idOrder, activity);
    }

    @Override
    public void updateDomiPosition(int idOrder, Activity activity) {
        repository.updateDomiPosition(idOrder, activity);
    }
}
