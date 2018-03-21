package co.domix.android.login.presenter;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.login.interactor.SignupInteractor;
import co.domix.android.login.interactor.SignupInteractorImpl;
import co.domix.android.login.view.Signup;
import co.domix.android.login.view.SignupView;

/**
 * Created by unicorn on 3/20/2018.
 */

public class SignupPresenterImpl implements SignupPresenter {

    private SignupView view;
    private SignupInteractor interactor;

    public SignupPresenterImpl(SignupView view) {
        this.view = view;
        interactor = new SignupInteractorImpl(this);
    }

    @Override
    public void signup(String email, String password, String confirmPassword, Signup signup, FirebaseAuth firebaseAuth) {
        interactor.signup(email, password, confirmPassword, signup, firebaseAuth);
    }

    @Override
    public void responseErrorSignup() {
        view.responseErrorSignup();
    }

    @Override
    public void responseSuccessSignup(String email) {
        view.responseSuccessSignup(email);
    }

    @Override
    public void responseCompleteAllFiles() {
        view.responseCompleteAllFiles();
    }

    @Override
    public void responseUnmatchPassword() {
        view.responseUnmatchPassword();
    }
}
