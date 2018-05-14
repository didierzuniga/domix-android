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
    public void responseTotalToPay(String currencyCode, int totalToPayCash, int pagado, double taxe,
                                   double fareDomix, int minPayment, double payUCommission, int payURate,
                                   String country, List<String> listOrders) {
        int commissionDomix = (int) (totalToPayCash * fareDomix);
        String payTaxe;
        String payTotalToDomix;
        int balanceToUpdate = -1;
        String miniPayment = "";
        boolean enableButtonPay;
        if (totalToPayCash != 0) {

            int payUCommissionCost = (int) (commissionDomix * payUCommission);
            int payUTotalCommission = payUCommissionCost + payURate;
            int payUIvaOverCommission = (int) (payUTotalCommission * taxe);
            int payUTotalCommissionWithIva = payUTotalCommission + payUIvaOverCommission;
            payTaxe = String.valueOf(payUTotalCommissionWithIva) + " " + currencyCode;

            int saldo = pagado - (commissionDomix + payUTotalCommissionWithIva);
            if (saldo >= 0){
                // Enviar a positive_balance --saldo--
                // Y mostrar 0 total a pagar
                balanceToUpdate = saldo;
                payTotalToDomix = "0.00 " + currencyCode;

                miniPayment = String.valueOf(minPayment) + " " + currencyCode;
                enableButtonPay = false;

            } else {
                //Enviar a positive_balance -- 0 --
                balanceToUpdate = 0;
                payTotalToDomix = String.valueOf(saldo * -1) + " " + currencyCode;
                if ((saldo * -1) >= minPayment){
                    enableButtonPay = true;
                } else {
                    miniPayment = String.valueOf(minPayment) + " " +currencyCode;
                    enableButtonPay = false;
                }
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
                                        enableButtonPay,
                                        balanceToUpdate,
                                        listOrders);
    }
}
