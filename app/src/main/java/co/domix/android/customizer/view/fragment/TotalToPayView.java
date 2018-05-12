package co.domix.android.customizer.view.fragment;

import java.util.List;

/**
 * Created by unicorn on 1/14/2018.
 */

public interface TotalToPayView {
    void showProgressBar();
    void hideProgressBar();
    void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix,
                                String minPayment, boolean enableButtonPay, List<String> listOrders);
    void thereAreNotOrders();
}
