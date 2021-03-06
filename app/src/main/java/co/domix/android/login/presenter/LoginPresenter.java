package co.domix.android.login.presenter;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.login.view.Login;
import co.domix.android.login.view.Signup;

/**
 * Created by unicorn on 11/10/2017.
 */

public interface LoginPresenter {
    void signin(String email, String password, Login login, FirebaseAuth firebaseAuth);
    void restorePassword(String email);
    void responseEnterEmail();

    void dismissDialogRestore();
    void resetPasswordSent();

    void goOrderCatched(String uid, String email, int idOrder);
    void goOrderRequested(String uid, String email, int idOrder);
    void goUserScore(String uid, String email, int idOrder);
    void goDomiciliaryScore(String uid, String email, int idOrder);
    void goHome(String uid, String email);

    void responseVerifyEmailFalse();

    void signinError(String err);
}
