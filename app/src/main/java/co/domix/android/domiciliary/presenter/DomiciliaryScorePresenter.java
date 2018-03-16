package co.domix.android.domiciliary.presenter;

import co.domix.android.domiciliary.view.DomiciliaryScore;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryScorePresenter {
    void sendScore(Double score, int idOrder, DomiciliaryScore domiciliaryScore);
    void responseBackDomiciliaryActivity();
}
