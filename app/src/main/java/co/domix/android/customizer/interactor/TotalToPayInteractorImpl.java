package co.domix.android.customizer.interactor;

import android.util.Log;

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
    public void responseTotalToPay(int totalToPayCash, double taxe, double fareDomix, int minPayment,
                                   double payUCommission, int payURate, String country) {
        String showCountry = "";
        if (country.equals("CO")){
            showCountry = "COP";
        } else if (country.equals("CL")){
            showCountry = "CLP";
        } else if (country.equals("MX")){
            showCountry = "MXN";
        }

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

            payTaxe = String.valueOf(payUTotalCommissionWithIva) + " " + showCountry;
            payTotalToDomix = String.valueOf(commissionDomix + payUTotalCommissionWithIva) + " " + showCountry;
            if ((commissionDomix + payUTotalCommissionWithIva) >= minPayment){
                enableButtonPay = true;
            } else {
                miniPayment = String.valueOf(minPayment) + " " +showCountry;
                enableButtonPay = false;
            }
        } else {
            payTaxe = "0.00 " + showCountry;
            payTotalToDomix = "0.00 " + showCountry;
            enableButtonPay = false;
        }
        presenter.responseTotalToPayCash(String.valueOf(commissionDomix) + " " + showCountry,
                                        payTaxe,
                                        payTotalToDomix,
                                        miniPayment,
                                        enableButtonPay);
    }
}
