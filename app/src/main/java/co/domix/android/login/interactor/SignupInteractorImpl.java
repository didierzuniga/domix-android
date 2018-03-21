package co.domix.android.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unicorn on 3/20/2018.
 */

public class SignupInteractorImpl implements SignupInteractor {
    @Override
    public void signup(String email, String password, String confirmPassword, Activity activity, FirebaseAuth firebaseAuth) {
        if (email.equals("") || password.equals("") || confirmPassword.equals("")) {
            presenter.responseCompleteAllFiles();
        } else if (!password.equals(confirmPassword)) {
            presenter.responseUnmatchPassword();
        } else {
            presenter.dismissDialogSignup();
            repository.signup(email, password, activity, firebaseAuth);
        }
    }
}
