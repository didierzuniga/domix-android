package co.domix.android.customizer.presenter;

import java.util.List;

/**
 * Created by unicorn on 1/14/2018.
 */

public interface TotalToPayPresenter {
    void queryOrderToPay(String uid, int payMethod);
    void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix,
                                String minPayment, boolean enableButtonPay, int balanceToUpdate,
                                List<String> listOrders);
    void thereAreNotOrders();
    void goPayU(List<String> list, int balance);
}
