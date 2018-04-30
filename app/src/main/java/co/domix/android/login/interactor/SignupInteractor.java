package co.domix.android.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unicorn on 3/20/2018.
 */

public interface SignupInteractor {
    void signup(String email, String password, String confirmPassword, String latitude, String longitude,
                Activity activity, FirebaseAuth firebaseAuth);
}
