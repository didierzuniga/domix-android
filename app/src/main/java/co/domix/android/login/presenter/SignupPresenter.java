package co.domix.android.login.presenter;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.login.view.Signup;

/**
 * Created by unicorn on 3/20/2018.
 */

public interface SignupPresenter {
    void signup(String email, String password, String confirmPassword, Signup signup, FirebaseAuth firebaseAuth);
    void responseErrorSignup();
    void responseSuccessSignup(String email);
    void responseCompleteAllFiles();
    void responseUnmatchPassword();
}
