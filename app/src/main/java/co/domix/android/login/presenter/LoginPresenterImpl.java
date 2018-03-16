package co.domix.android.login.presenter;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.login.interactor.LoginInteractor;
import co.domix.android.login.interactor.LoginInteractorImpl;
import co.domix.android.login.view.Login;
import co.domix.android.login.view.LoginView;

/**
 * Created by unicorn on 11/10/2017.
 */

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView view;
    private LoginInteractor interactor;

    public LoginPresenterImpl(LoginView view) {
        this.view = view;
        interactor = new LoginInteractorImpl(this);
    }

    @Override
    public void signup(String email, String password, String confirmPassword, Login login, FirebaseAuth firebaseAuth) {
        interactor.signup(email, password, confirmPassword, login, firebaseAuth);
    }

    @Override
    public void signin(String email, String password, Login login, FirebaseAuth firebaseAuth) {
        interactor.signin(email, password, login, firebaseAuth);
    }

    @Override
    public void restorePassword(String email) {
        interactor.restorePassword(email);
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
    public void responseEnterEmail() {
        view.responseEnterEmail();
    }

    @Override
    public void responseCompleteAllFiles() {
        view.responseCompleteAllFiles();
    }

    @Override
    public void responseUnmatchPassword() {
        view.responseUnmatchPassword();
    }

    @Override
    public void dismissDialogSignup() {
        view.dismissDialogSignup();
    }

    @Override
    public void dismissDialogRestore() {
        view.dismissDialogRestore();
    }

    @Override
    public void resetPasswordSent() {
        view.resetPasswordSent();
    }

    @Override
    public void goOrderCatched(String uid, String email, int idOrder) {
        view.goOrderCatched(uid, email, idOrder);
    }

    @Override
    public void goOrderRequested(String uid, String email, int idOrder) {
        view.goOrderRequested(uid, email, idOrder);
    }

    @Override
    public void goUserScore(String uid, String email, int idOrder) {
        view.goUserScore(uid, email, idOrder);
    }

    @Override
    public void goDomiciliaryScore(String uid, String email, int idOrder) {
        view.goDomiciliaryScore(uid, email, idOrder);
    }

    @Override
    public void goHome(String uid, String email) {
        view.goHome(uid, email);
    }

    @Override
    public void responseVerifyEmailFalse() {
        view.responseVerifyEmailFalse();
    }

    @Override
    public void signinError(String err) {
        view.signinError(err);
    }
}
