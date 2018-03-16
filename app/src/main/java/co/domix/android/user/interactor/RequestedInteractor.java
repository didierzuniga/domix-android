package co.domix.android.user.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface RequestedInteractor {
    void listenForUpdate(int idOrder, Activity activity);
    void dialogCancel(int idOrder, Activity activity);
    void updateDomiPosition(int idOrder, Activity activity);
}
