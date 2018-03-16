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
    public void responseTotalToPay(int totalToPayCash, int totalToPayEcoin, double taxe,
                                   double payUCommission, int payURate, String country) {
        String showCountry = "";
        if (country.equals("CO")){
            showCountry = "COP";
        } else if (country.equals("CL")){
            showCountry = "CLP";
        } else if (country.equals("MX")){
            showCountry = "MXN";
        }
        if (payXXX == 4){
            presenter.responseTotalToPayEcoin(String.valueOf(totalToPayEcoin));
        } else {
            int commissionDomix = (int)(totalToPayCash * 0.37);
            String payTaxe;
            String payTotalToDomix;
            if (totalToPayCash != 0){
                int payUCommissionCost = (int)(commissionDomix * payUCommission);
                int payUTotalCommission = payUCommissionCost + payURate;
                int payUIvaOverCommission = (int)(payUTotalCommission * taxe);
                int payUTotalCommissionWithIva = payUTotalCommission + payUIvaOverCommission;
                payTaxe = String.valueOf(payUTotalCommissionWithIva) + " " + showCountry;
                payTotalToDomix = String.valueOf(commissionDomix + payUTotalCommissionWithIva) + " " + showCountry;
            } else {
                payTaxe = "0 " + showCountry;
                payTotalToDomix = "0 " + showCountry;
            }
            presenter.responseTotalToPayCash(String.valueOf(commissionDomix) + " " + showCountry,
                                            payTaxe,
                                            payTotalToDomix);
        }
    }
}
