package co.domix.android.domiciliary.repository;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryScoreRepository {
    void sendScore(Double score, int idOrder, Activity activity);
    void insertRate(Double score, String uidAuthor);
}
