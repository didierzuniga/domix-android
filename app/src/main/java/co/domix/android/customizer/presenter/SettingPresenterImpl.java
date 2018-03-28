package co.domix.android.customizer.presenter;

import co.domix.android.customizer.interactor.SettingInteractor;
import co.domix.android.customizer.interactor.SettingInteractorImpl;
import co.domix.android.customizer.view.SettingView;

/**
 * Created by unicorn on 3/27/2018.
 */

public class SettingPresenterImpl implements SettingPresenter {

    private SettingView view;
    private SettingInteractor interactor;

    public SettingPresenterImpl(SettingView view) {
        this.view = view;
        interactor = new SettingInteractorImpl(this);
    }

    @Override
    public void dialogReauthenticate(String email, String password, int opt) {
        interactor.dialogReauthenticate(email, password, opt);
    }

    @Override
    public void goToChangePassword() {
        view.goToChangePassword();
    }

    @Override
    public void changePassword(String newPassword) {
        interactor.changePassword(newPassword);
    }
}
