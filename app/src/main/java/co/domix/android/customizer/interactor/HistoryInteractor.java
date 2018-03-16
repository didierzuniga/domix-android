package co.domix.android.customizer.interactor;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

/**
 * Created by unicorn on 1/30/2018.
 */

public interface HistoryInteractor {
    void listOrder(Activity activity, RecyclerView rv, String uid);
}
