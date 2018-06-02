package co.domix.android.customizer.presenter;


import java.util.List;

import co.domix.android.customizer.interactor.TotalToPayInteractor;
import co.domix.android.customizer.interactor.TotalToPayInteractorImpl;
import co.domix.android.customizer.view.AmountToPayView;

/**
 * Created by unicorn on 1/14/2018.
 */

public class TotalToPayPresenterImpl implements TotalToPayPresenter {

    private AmountToPayView view;
    private TotalToPayInteractor interactor;

    public TotalToPayPresenterImpl(AmountToPayView view) {
        this.view = view;
        interactor = new TotalToPayInteractorImpl(this);
    }

    @Override
    public void queryOrderToPay(String uid, int payMethod) {
        interactor.queryOrderToPay(uid, payMethod);
    }

    @Override
    public void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix,
                                       String minPayment, boolean enableButtonPay, int balanceToUpdate,
                                       List<String> listOrders, String currencyCode) {
        view.responseTotalToPayCash(commissionDomix, payTaxe, payTotalToDomix, minPayment, enableButtonPay,
                                    balanceToUpdate, listOrders, currencyCode);
    }

    @Override
    public void thereAreNotOrders() {
        view.thereAreNotOrders();
    }

    @Override
    public void goPayU(List<String> list, int balance) {
        interactor.goPayU(list, balance);
    }
}
