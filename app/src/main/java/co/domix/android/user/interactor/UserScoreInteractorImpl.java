package co.domix.android.user.interactor;

import android.app.Activity;

import co.domix.android.user.presenter.UserScorePresenter;
import co.domix.android.user.repository.UserScoreRepository;
import co.domix.android.user.repository.UserScoreRepositoryImpl;

/**
 * Created by unicorn on 11/13/2017.
 */

public class UserScoreInteractorImpl implements UserScoreInteractor {

    private UserScorePresenter presenter;
    private UserScoreRepository repository;

    public UserScoreInteractorImpl(UserScorePresenter presenter) {
        this.presenter = presenter;
        repository = new UserScoreRepositoryImpl(presenter);
    }

    @Override
    public void sendScore(Double score, int idOrder, Activity activity) {
        repository.sendScore(score, idOrder, activity);
    }
}
