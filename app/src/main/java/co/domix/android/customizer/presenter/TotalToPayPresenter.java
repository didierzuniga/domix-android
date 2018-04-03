package co.domix.android.customizer.presenter;

/**
 * Created by unicorn on 1/14/2018.
 */

public interface TotalToPayPresenter {
    void queryOrderToPay(String uid, int payMethod);
    void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix, boolean enableButtonPay);
}
