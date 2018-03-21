package co.domix.android.login.view;

/**
 * Created by unicorn on 11/9/2017.
 */

public interface LoginView {
    void enableInputs();
    void disableInputs();

    void showProgressBar();
    void hideProgressBar();

    void signinError(String err);
    void goOrderCatched(String uid, String email, int idOrder);
    void goOrderRequested(String uid, String email, int idOrder);
    void goUserScore(String uid, String email, int idOrder);
    void goDomiciliaryScore(String uid, String email, int idOrder);

    void goHome(String uid, String email);

    void signin(String email, String password);

    void responseErrorSignup();
    void responseSuccessSignup(String email);
    void responseEnterEmail();
    void responseCompleteAllFiles();
    void responseUnmatchPassword();

    void dismissDialogRestore();
    void dismissDialogSignup();
    void resetPasswordSent();
    void responseVerifyEmailFalse();
}
