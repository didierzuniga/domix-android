package co.domix.android.customizer.interactor;

import android.content.Intent;
import android.util.Log;

import co.domix.android.customizer.presenter.ProfilePresenter;
import co.domix.android.customizer.repository.ProfileRepository;
import co.domix.android.customizer.repository.ProfileRepositoryImpl;

/**
 * Created by unicorn on 12/18/2017.
 */

public class ProfileInteractorImpl implements ProfileInteractor {

    private ProfilePresenter presenter;
    private ProfileRepository repository;

    public ProfileInteractorImpl(ProfilePresenter presenter) {
        this.presenter = presenter;
        repository = new ProfileRepositoryImpl(presenter, this);
    }

    @Override
    public void queryImageSeted(String uid) {
        repository.queryImageSeted(uid);
    }

    @Override
    public void trueImageSeted(String uid) {
        repository.trueImageSeted(uid);
    }

    @Override
    public void responseDataUser(boolean verifyGlide, String firstname, String lastname, String email, String scoreAsDomi,
                                 String scoreAsUser) {
        if (firstname != null && lastname != null){
            presenter.responseDataUser(verifyGlide, firstname, lastname, email, scoreAsDomi, scoreAsUser);
        } else {
            presenter.responseDataUser(verifyGlide, null, null, email, "0.00", "0.00");
        }
    }
}
