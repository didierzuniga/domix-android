package co.domix.android.login.interactor;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import co.domix.android.login.presenter.SignupPresenter;
import co.domix.android.login.repository.SignupRepository;
import co.domix.android.login.repository.SignupRepositoryImpl;

/**
 * Created by unicorn on 3/20/2018.
 */

public class SignupInteractorImpl implements SignupInteractor {

    private List<Address> geocodeMatches = null;
    private SignupPresenter presenter;
    private SignupRepository repository;

    public SignupInteractorImpl(SignupPresenter presenter) {
        this.presenter = presenter;
        repository = new SignupRepositoryImpl(presenter);
    }

    @Override
    public void signup(String email, String password, String confirmPassword, String latitude, String longitude,
                       Activity activity, FirebaseAuth firebaseAuth) {
        String codeCountry = "";
        try {
            geocodeMatches = new Geocoder(activity).getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!geocodeMatches.isEmpty()) {
            codeCountry = geocodeMatches.get(0).getCountryCode();
        }
        repository.signup(email, password, codeCountry, activity, firebaseAuth);
    }
}
