package co.domix.android.customizer.presenter;


import co.domix.android.customizer.interactor.TotalToPayInteractor;
import co.domix.android.customizer.interactor.TotalToPayInteractorImpl;
import co.domix.android.customizer.view.fragment.TotalToPayView;

/**
 * Created by unicorn on 1/14/2018.
 */

public class TotalToPayPresenterImpl implements TotalToPayPresenter {

    private TotalToPayView view;
    private TotalToPayInteractor interactor;

    public TotalToPayPresenterImpl(TotalToPayView view) {
        this.view = view;
        interactor = new TotalToPayInteractorImpl(this);
    }

    @Override
    public void queryOrderToPay(String uid, int payMethod) {
        interactor.queryOrderToPay(uid, payMethod);
    }

    @Override
    public void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix) {
        view.responseTotalToPayCash(commissionDomix, payTaxe, payTotalToDomix);
    }
}
