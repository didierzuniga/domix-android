package co.domix.android.domiciliary.presenter;

import co.domix.android.domiciliary.interactor.DomiciliaryScoreInteractor;
import co.domix.android.domiciliary.interactor.DomiciliaryScoreInteractorImpl;
import co.domix.android.domiciliary.view.DomiciliaryScore;
import co.domix.android.domiciliary.view.DomiciliaryScoreView;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryScorePresenterImpl implements DomiciliaryScorePresenter {

    private DomiciliaryScoreView view;
    private DomiciliaryScoreInteractor interactor;

    public DomiciliaryScorePresenterImpl(DomiciliaryScoreView view) {
        this.view = view;
        interactor = new DomiciliaryScoreInteractorImpl(this);
    }

    @Override
    public void sendScore(Double score, int idOrder, DomiciliaryScore domiciliaryScore) {
        interactor.sendScore(score, idOrder, domiciliaryScore);
    }

    @Override
    public void responseBackDomiciliaryActivity() {
        view.responseBackDomiciliaryActivity();
    }
}
