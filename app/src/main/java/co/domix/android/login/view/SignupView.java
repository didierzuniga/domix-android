package co.domix.android.login.view;

/**
 * Created by unicorn on 3/20/2018.
 */

public interface SignupView {
    void showProgressBar();
    void hideProgressBar();
    void signup(String email, String password, String confirmPassword);
    void responseCompleteAllFiles();
    void responseUnmatchPassword();
    void responseErrorSignup();
    void responseSuccessSignup();
}
