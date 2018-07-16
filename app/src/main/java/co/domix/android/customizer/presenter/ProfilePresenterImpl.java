package co.domix.android.customizer.presenter;

import co.domix.android.customizer.interactor.ProfileInteractor;
import co.domix.android.customizer.interactor.ProfileInteractorImpl;
import co.domix.android.customizer.view.ProfileView;

/**
 * Created by unicorn on 12/18/2017.
 */

public class ProfilePresenterImpl implements ProfilePresenter {

    private ProfileView view;
    private ProfileInteractor interactor;

    public ProfilePresenterImpl(ProfileView view){
        this.view = view;
        interactor = new ProfileInteractorImpl(this);
    }
    @Override
    public void queryImageSeted(String uid, boolean searchImage) {
        interactor.queryImageSeted(uid, searchImage);
    }

    @Override
    public void responseDataUser(boolean verifyGlide, String firstname, String lastname, String dni,
                                 String phone, String email, String scoreAsDomi, String scoreAsUser, String credit) {
        view.responseDataUser(verifyGlide, firstname, lastname, dni, phone, email, scoreAsDomi, scoreAsUser, credit);
    }

    @Override
    public void trueImageSeted(String uid) {
        interactor.trueImageSeted(uid);
    }
}
