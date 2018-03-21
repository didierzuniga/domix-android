package co.domix.android.login.presenter;

import com.google.firebase.auth.FirebaseAuth;

import co.domix.android.login.view.Signup;
import co.domix.android.login.view.SignupView;

/**
 * Created by unicorn on 3/20/2018.
 */

public class SignupPresenterImpl implements SignupPresenter {

    private SignupView view;

    public SignupPresenterImpl(SignupView view) {
        this.view = view;
    }

    @Override
    public void signup(String email, String password, String confirmPassword, Signup signup, FirebaseAuth firebaseAuth) {
        interactor.signup(email, password, confirmPassword, signup, firebaseAuth);
    }
}
