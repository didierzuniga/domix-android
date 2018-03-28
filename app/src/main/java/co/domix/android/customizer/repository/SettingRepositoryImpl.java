package co.domix.android.customizer.repository;

import co.domix.android.customizer.interactor.SettingInteractor;
import co.domix.android.customizer.presenter.SettingPresenter;

/**
 * Created by unicorn on 3/27/2018.
 */

public class SettingRepositoryImpl implements SettingRepository {
    private SettingPresenter presenter;

    public SettingRepositoryImpl(SettingPresenter presenter) {
        this.presenter = presenter;
    }
}
