package co.domix.android.user.presenter;

import co.domix.android.user.view.UserScore;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface UserScorePresenter {
    void sendScore(Double score, int idOrder, UserScore userScore);
    void responseBackHomeActivity();
}
