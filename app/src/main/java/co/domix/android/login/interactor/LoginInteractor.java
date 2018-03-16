package co.domix.android.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unicorn on 11/10/2017.
 */

public interface LoginInteractor {
    void signup(String email, String password, String confirmPassword, Activity activity, FirebaseAuth firebaseAuth);
    void signin(String email, String password, Activity activity, FirebaseAuth firebaseAuth);
    void restorePassword(String email);
}
