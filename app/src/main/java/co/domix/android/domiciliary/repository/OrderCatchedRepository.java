package co.domix.android.domiciliary.repository;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface OrderCatchedRepository {
    void getUserRequest(int idOrder, String uid, Activity activity);
    void getNameAndPhoneAuthor(String uidAuthor, String countryAuthor, String cityAuthor, String fromAuthor,
                               String toAuthor, String titleAuthor, String descriptionAuthor,
                               String oriLa, String oriLo, String desLa, String desLo, int moneyAuthor);
    void dialogCancel(String idOrder, String uid, Activity activity);
    void dialogFinish(String idOrder, String uidDomicili, Activity activity);
    void submitCoord(String uid, String la, String lo, Activity activity);
    void verifyOrderActive(String uid, Activity activity);
    void removeCoordDomiciliary(String id);
    void deductCounterRealtime();
}
