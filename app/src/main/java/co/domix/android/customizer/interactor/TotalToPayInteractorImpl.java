package co.domix.android.customizer.interactor;

import android.util.Log;

import java.util.List;

import co.domix.android.customizer.presenter.TotalToPayPresenter;
import co.domix.android.customizer.repository.TotalToPayRepository;
import co.domix.android.customizer.repository.TotalToPayRepositoryImpl;

/**
 * Created by unicorn on 1/14/2018.
 */

public class TotalToPayInteractorImpl implements TotalToPayInteractor {

    private TotalToPayPresenter presenter;
    private TotalToPayRepository repository;
    private int payXXX;

    public TotalToPayInteractorImpl(TotalToPayPresenter presenter) {
        this.presenter = presenter;
        repository = new TotalToPayRepositoryImpl(presenter, this);
    }

    @Override
    public void queryOrderToPay(String uid, int payMethod) {
        payXXX = payMethod;
        repository.queryOrderToPay(uid);
    }

    @Override
    public void responseTotalToPay(String currencyCode, int totalToPayCash, double taxe, double fareDomix, int minPayment,
                                   double payUCommission, int payURate, String country, List<String> listOrders) {

        int commissionDomix = (int) (totalToPayCash * fareDomix);
        String payTaxe;
        String payTotalToDomix;
        String miniPayment = "";
        boolean enableButtonPay;
        if (totalToPayCash != 0) {

            int payUCommissionCost = (int) (commissionDomix * payUCommission);
            int payUTotalCommission = payUCommissionCost + payURate;
            int payUIvaOverCommission = (int) (payUTotalCommission * taxe);
            int payUTotalCommissionWithIva = payUTotalCommission + payUIvaOverCommission;

            payTaxe = String.valueOf(payUTotalCommissionWithIva) + " " + currencyCode;
            payTotalToDomix = String.valueOf(commissionDomix + payUTotalCommissionWithIva) + " " + currencyCode;
            if ((commissionDomix + payUTotalCommissionWithIva) >= minPayment){
                enableButtonPay = true;
            } else {
                miniPayment = String.valueOf(minPayment) + " " +currencyCode;
                enableButtonPay = false;
            }
        } else {
            payTaxe = "0.00 " + currencyCode;
            payTotalToDomix = "0.00 " + currencyCode;
            enableButtonPay = false;
        }
        presenter.responseTotalToPayCash(String.valueOf(commissionDomix) + " " + currencyCode,
                                        payTaxe,
                                        payTotalToDomix,
                                        miniPayment,
                                        enableButtonPay);
    }
}
