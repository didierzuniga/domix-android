package co.domix.android.domiciliary.interactor;

import android.app.Activity;

import co.domix.android.domiciliary.presenter.DomiciliaryScorePresenter;
import co.domix.android.domiciliary.repository.DomiciliaryScoreRepository;
import co.domix.android.domiciliary.repository.DomiciliaryScoreRepositoryImpl;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryScoreInteractorImpl implements DomiciliaryScoreInteractor {

    private DomiciliaryScorePresenter presenter;
    private DomiciliaryScoreRepository repository;

    public DomiciliaryScoreInteractorImpl(DomiciliaryScorePresenter presenter) {
        this.presenter = presenter;
        repository = new DomiciliaryScoreRepositoryImpl(presenter);
    }

    @Override
    public void sendScore(Double score, int idOrder, Activity activity) {
        repository.sendScore(score, idOrder, activity);
    }
}
