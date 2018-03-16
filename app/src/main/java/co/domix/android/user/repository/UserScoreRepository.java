package co.domix.android.user.repository;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface UserScoreRepository {
    void sendScore(Double score, int idOrder, Activity activity);
    void insertScore(Double score, String idDomiciliary);
}
