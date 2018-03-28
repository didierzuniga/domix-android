package co.domix.android.customizer.interactor;

import co.domix.android.customizer.presenter.SettingPresenter;
import co.domix.android.customizer.repository.SettingRepository;
import co.domix.android.customizer.repository.SettingRepositoryImpl;

/**
 * Created by unicorn on 3/27/2018.
 */

public class SettingInteractorImpl implements SettingInteractor {
    private SettingPresenter presenter;
    private SettingRepository repository;

    public SettingInteractorImpl(SettingPresenter presenter) {
        this.presenter = presenter;
        repository = new SettingRepositoryImpl(presenter);
    }
}
