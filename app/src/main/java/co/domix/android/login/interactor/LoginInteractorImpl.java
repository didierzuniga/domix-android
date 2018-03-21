package co.domix.android.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.login.presenter.LoginPresenter;
import co.domix.android.login.repository.LoginRepository;
import co.domix.android.login.repository.LoginRepositoryImpl;

/**
 * Created by unicorn on 11/11/2017.
 */

public class LoginInteractorImpl implements LoginInteractor {

    private LoginPresenter presenter;
    private LoginRepository repository;

    public LoginInteractorImpl(LoginPresenter presenter) {
        this.presenter = presenter;
        repository = new LoginRepositoryImpl(presenter);
    }

    @Override
    public void signin(String email, String password, Activity activity, FirebaseAuth firebaseAuth) {
        repository.signin(email, password, activity, firebaseAuth);
    }

    @Override
    public void restorePassword(String email) {
        if (email.equals("")) {
            presenter.responseEnterEmail();
        } else {
            presenter.dismissDialogRestore();
            repository.restorePassword(email);
        }
    }
}
