package co.domix.android.domiciliary.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryScoreInteractor {
    void sendScore(Double score, int idOrder, Activity activity);
}
