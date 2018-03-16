package co.domix.android.customizer.interactor;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import co.domix.android.customizer.presenter.HistoryPresenter;
import co.domix.android.customizer.repository.HistoryRepository;
import co.domix.android.customizer.repository.HistoryRepositoryImpl;

/**
 * Created by unicorn on 1/30/2018.
 */

public class HistoryInteractorImpl implements HistoryInteractor {

    private HistoryPresenter presenter;
    private HistoryRepository repository;

    public HistoryInteractorImpl(HistoryPresenter presenter) {
        this.presenter = presenter;
        repository = new HistoryRepositoryImpl(presenter);
    }

    @Override
    public void listOrder(Activity activity, RecyclerView rv, String uid) {
        repository.listOrder(activity, rv, uid);
    }
}
