package co.domix.android.user.presenter;

import co.domix.android.user.interactor.UserScoreInteractor;
import co.domix.android.user.interactor.UserScoreInteractorImpl;
import co.domix.android.user.view.UserScore;
import co.domix.android.user.view.UserScoreView;

/**
 * Created by unicorn on 11/13/2017.
 */

public class UserScorePresenterImpl implements UserScorePresenter {

    private UserScoreView view;
    private UserScoreInteractor interactor;

    public UserScorePresenterImpl(UserScoreView view) {
        this.view = view;
        interactor = new UserScoreInteractorImpl(this);
    }

    @Override
    public void sendScore(Double score, int idOrder, UserScore userScore) {
        interactor.sendScore(score, idOrder, userScore);
    }

    @Override
    public void responseBackHomeActivity() {
        view.responseBackHomeActivity();
    }
}
