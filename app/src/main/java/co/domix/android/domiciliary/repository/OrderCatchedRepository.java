package co.domix.android.domiciliary.repository;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface OrderCatchedRepository {
    void getUserRequest(int idOrder, String uid, Activity activity);
    void getNameAndPhoneAuthor(String uidAuthor, String countryAuthor, String cityAuthor, String fromAuthor,
                               String toAuthor, String titleAuthor, String descriptionAuthor, String origenCoordinate,
                               String destineCoordinate, int moneyCash, int moneyCredit, int paymentMethod);
    void dialogCancel(String idOrder, String uid, Activity activity);
    void dialogFinish(String idOrder, String uid);
    void submitCoord(String uid, String la, String lo, Activity activity);
    void verifyStatusOrder(String uid, String idorder, Activity activity);
    void removeCoordDomiciliary(String id);
    void modifyCounterRealtimeAndDone();
}
