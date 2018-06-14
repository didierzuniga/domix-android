package co.domix.android.customizer.interactor;

import android.util.Log;

import java.text.DecimalFormat;
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
    public void responseTotalToPay(String currencyCode, int fareToPayDomix, int pagado, double taxe,
                                   int minPayment, double payUCommission, int payURate, String country,
                                   List<String> listOrders) {
        DecimalFormat formatMiles = new DecimalFormat("###,###.##");
        String payTaxe;
        String payTotalToDomix;
        int balanceToUpdate = -1;
        String miniPayment = "";
        boolean enableButtonPay;
        if (fareToPayDomix != 0) {

            int payUCommissionCost = (int) (fareToPayDomix * payUCommission);
            int payUTotalCommission = payUCommissionCost + payURate;
            int payUIvaOverCommission = (int) (payUTotalCommission * taxe);
            int payUTotalCommissionWithIva = payUTotalCommission + payUIvaOverCommission;
            payTaxe = formatMiles.format(payUTotalCommissionWithIva);
//            payTaxe = String.valueOf(payUTotalCommissionWithIva);

            int saldo = pagado - (fareToPayDomix + payUTotalCommissionWithIva);
            if (saldo >= 0){
                // Enviar a positive_balance --saldo--
                // Y mostrar 0 total a pagar
                balanceToUpdate = saldo;
                payTotalToDomix = "0.00 ";
                miniPayment = String.valueOf(minPayment);
                enableButtonPay = false;

            } else {
                //Enviar a positive_balance -- 0 --
                balanceToUpdate = 0;
                payTotalToDomix = formatMiles.format(saldo * -1);
//                payTotalToDomix = String.valueOf(saldo * -1);
                if ((saldo * -1) >= minPayment){
                    enableButtonPay = true;
                } else {
                    miniPayment = String.valueOf(minPayment);
                    enableButtonPay = false;
                }
            }
        } else {
            payTaxe = "0.00 ";
            payTotalToDomix = "0.00 ";
            enableButtonPay = false;
        }
        presenter.responseTotalToPayCash(formatMiles.format(fareToPayDomix),
                                        payTaxe,
                                        payTotalToDomix,
                                        miniPayment,
                                        enableButtonPay,
                                        balanceToUpdate,
                                        listOrders,
                                        currencyCode);
    }

    @Override
    public void goPayU(List<String> list, int balance) {
        repository.goPayU(list, balance);
    }
}
