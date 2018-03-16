package co.domix.android.user.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface UserScoreInteractor {
    void sendScore(Double score, int idOrder, Activity activity);
}
