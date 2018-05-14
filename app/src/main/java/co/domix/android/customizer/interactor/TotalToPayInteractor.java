package co.domix.android.customizer.interactor;

import java.util.List;

/**
 * Created by unicorn on 1/14/2018.
 */

public interface TotalToPayInteractor {
    void queryOrderToPay(String uid, int payMethod);
    void responseTotalToPay(String currencyCode, int totalToPayCash, int pagado, double taxe, double fareDomix,
                            int minPayment, double payUCommission, int payURate, String country, List<String> listOrders);
}
