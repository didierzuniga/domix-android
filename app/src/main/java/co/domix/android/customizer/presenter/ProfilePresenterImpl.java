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
    public void queryImageSeted(String uid) {
        interactor.queryImageSeted(uid);
    }

    @Override
    public void responseDataUser(boolean verifyGlide, String firstname, String lastname, String email, String scoreAsDomi,
                                 String scoreAsUser) {
        view.responseDataUser(verifyGlide, firstname, lastname, email, scoreAsDomi, scoreAsUser);
    }

    @Override
    public void trueImageSeted(String uid) {
        interactor.trueImageSeted(uid);
    }
}
