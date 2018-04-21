package co.domix.android.domiciliary.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface OrderCatchedInteractor {
    void getUserRequest(int idOrder, String uid, Activity activity);
    void dialogCancel(String idOrder, String uid, Activity activity);
    void dialogFinish(String idOrder);
    void submitCoord(String uid, String la, String lo, Activity activity);
}
