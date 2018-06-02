package co.domix.android.customizer.view;

import java.util.List;

/**
 * Created by unicorn on 6/1/2018.
 */

public interface AmountToPayView {
    void showProgressBar();
    void hideProgressBar();
    void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix,
                                String minPayment, boolean enableButtonPay, int balanceToUpdate,
                                List<String> listOrders, String currencyCode);
    void thereAreNotOrders();
    void goHome();
    void goProfile();
    void goHistory();
    void goSetting();
    void goPayment();
    void logOut();
}
