package co.domix.android.customizer.view;

import android.support.v7.widget.RecyclerView;

/**
 * Created by unicorn on 1/30/2018.
 */

public interface HistoryView {
    void listOrder(RecyclerView rv);
    void goHome();
    void goProfile();
    void goHistory();
    void goSetting();
    void goPayment();
    void logOut();
}
