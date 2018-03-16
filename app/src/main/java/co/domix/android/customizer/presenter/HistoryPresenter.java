package co.domix.android.customizer.presenter;

import android.support.v7.widget.RecyclerView;

import co.domix.android.customizer.view.History;

/**
 * Created by unicorn on 1/30/2018.
 */

public interface HistoryPresenter {
    void listOrder(History history, RecyclerView rv, String uid);
}
