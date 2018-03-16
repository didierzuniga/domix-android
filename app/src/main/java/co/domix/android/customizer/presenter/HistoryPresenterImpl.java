package co.domix.android.customizer.presenter;

import android.support.v7.widget.RecyclerView;

import co.domix.android.customizer.interactor.HistoryInteractor;
import co.domix.android.customizer.interactor.HistoryInteractorImpl;
import co.domix.android.customizer.view.History;
import co.domix.android.customizer.view.HistoryView;

/**
 * Created by unicorn on 1/30/2018.
 */

public class HistoryPresenterImpl implements HistoryPresenter {

    private HistoryView view;
    private HistoryInteractor interactor;

    public HistoryPresenterImpl(HistoryView view) {
        this.view = view;
        interactor = new HistoryInteractorImpl(this);
    }

    @Override
    public void listOrder(History history, RecyclerView rv, String uid) {
        interactor.listOrder(history, rv, uid);
    }
}
