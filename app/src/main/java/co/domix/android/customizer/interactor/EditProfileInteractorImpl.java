package co.domix.android.customizer.interactor;

import co.domix.android.customizer.presenter.EditProfilePresenter;
import co.domix.android.customizer.repository.EditProfileRepository;
import co.domix.android.customizer.repository.EditProfileRepositoryImpl;

/**
 * Created by unicorn on 5/30/2018.
 */

public class EditProfileInteractorImpl implements EditProfileInteractor {

    private EditProfilePresenter presenter;
    private EditProfileRepository repository;

    public EditProfileInteractorImpl(EditProfilePresenter presenter) {
        this.presenter = presenter;
        repository = new EditProfileRepositoryImpl(presenter, this);
    }

    @Override
    public void changePersonalData(String uid, int field, String data) {
        repository.changePersonalData(uid, field, data);
    }
}
