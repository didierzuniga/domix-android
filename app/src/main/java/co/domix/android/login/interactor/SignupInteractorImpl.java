package co.domix.android.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.login.presenter.SignupPresenter;
import co.domix.android.login.repository.SignupRepository;
import co.domix.android.login.repository.SignupRepositoryImpl;

/**
 * Created by unicorn on 3/20/2018.
 */

public class SignupInteractorImpl implements SignupInteractor {

    private SignupPresenter presenter;
    private SignupRepository repository;

    public SignupInteractorImpl(SignupPresenter presenter) {
        this.presenter = presenter;
        repository = new SignupRepositoryImpl(presenter);
    }

    @Override
    public void signup(String email, String password, String confirmPassword, Activity activity, FirebaseAuth firebaseAuth) {
        if (email.equals("") || password.equals("") || confirmPassword.equals("")) {
            presenter.responseCompleteAllFiles();
        } else if (!password.equals(confirmPassword)) {
            presenter.responseUnmatchPassword();
        } else {
            repository.signup(email, password, activity, firebaseAuth);
        }
    }
}
