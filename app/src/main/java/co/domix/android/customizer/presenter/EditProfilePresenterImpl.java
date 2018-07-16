package co.domix.android.customizer.presenter;

import co.domix.android.customizer.interactor.EditProfileInteractor;
import co.domix.android.customizer.interactor.EditProfileInteractorImpl;
import co.domix.android.customizer.view.EditProfileView;

/**
 * Created by unicorn on 5/30/2018.
 */

public class EditProfilePresenterImpl implements EditProfilePresenter {

    private EditProfileView view;
    private EditProfileInteractor interactor;

    public EditProfilePresenterImpl(EditProfileView view) {
        this.view = view;
        interactor = new EditProfileInteractorImpl(this);
    }


    @Override
    public void changePersonalData(String uid, int field, String data) {
        interactor.changePersonalData(uid, field, data);
    }

    @Override
    public void dataChangeSuccess() {
        view.dataChangeSuccess();
    }
}
