package co.domix.android.login.repository;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unicorn on 11/11/2017.
 */

public interface LoginRepository {
    void signup(String email, String password, Activity activity, FirebaseAuth firebaseAuth);
    void signin(String email, String password, Activity activity, FirebaseAuth firebaseAuth);
    void restorePassword(String email);
    void queryStatePosition(String uid, String email, Activity activity);
    void sendEmailVerification(String email, String uid, Activity activity);
    void setDataUser(String email, String uid, Activity activity);
}
