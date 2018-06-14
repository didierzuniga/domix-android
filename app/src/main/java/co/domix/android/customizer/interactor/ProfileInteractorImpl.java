package co.domix.android.customizer.interactor;

import android.content.Intent;
import android.util.Log;

import java.text.DecimalFormat;

import co.domix.android.customizer.presenter.ProfilePresenter;
import co.domix.android.customizer.repository.ProfileRepository;
import co.domix.android.customizer.repository.ProfileRepositoryImpl;

/**
 * Created by unicorn on 12/18/2017.
 */

public class ProfileInteractorImpl implements ProfileInteractor {

    private ProfilePresenter presenter;
    private ProfileRepository repository;
    private String first, last, document, cell;

    public ProfileInteractorImpl(ProfilePresenter presenter) {
        this.presenter = presenter;
        repository = new ProfileRepositoryImpl(presenter, this);
    }

    @Override
    public void queryImageSeted(String uid) {
        repository.queryImageSeted(uid);
    }

    @Override
    public void trueImageSeted(String uid) {
        repository.trueImageSeted(uid);
    }

    @Override
    public void responseDataUser(boolean verifyGlide, String firstname, String lastname, String dni,
                                 String phone, String email, float scoreAsDomi, float scoreAsUser, int credit,
                                 String currency) {
        DecimalFormat formatMiles = new DecimalFormat("###,###.##");
        if (firstname == null && lastname == null && dni == null && phone == null){
            presenter.responseDataUser(verifyGlide, null, null, null, null,
                    email, "0.00", "0.00", formatMiles.format(credit) + " " + currency);
        } else {
            if (firstname == null || lastname == null || dni == null || phone == null){
                first = firstname;
                last = lastname;
                document = dni;
                cell = phone;
                presenter.responseDataUser(verifyGlide, first, last, document, cell, email, String.valueOf(scoreAsDomi),
                        String.valueOf(scoreAsUser), formatMiles.format(credit) + " " + currency);
            } else {
                presenter.responseDataUser(verifyGlide, firstname, lastname, dni, phone, email, String.valueOf(scoreAsDomi),
                        String.valueOf(scoreAsUser), formatMiles.format(credit) + " " + currency);
            }
        }
    }
}
