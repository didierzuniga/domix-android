package co.domix.android.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by unicorn on 11/22/2017.
 */

public interface SplashInteractor {
    void queryStatePosition(String uid, Activity activity);
    void verifyNetworkAndInternet(Activity activity, boolean isOnline, FirebaseUser firebaseUser, String uid);
}
