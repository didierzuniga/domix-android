package co.domix.android.login.repository;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unicorn on 3/20/2018.
 */

public interface SignupRepository {
    void signup(String email, String password, String codeCountry, Activity activity, FirebaseAuth firebaseAuth);
    void sendEmailVerification(String email, String uid, String codeCountry);
    void setDataUser(String email, String uid, String codeCountry);
}
