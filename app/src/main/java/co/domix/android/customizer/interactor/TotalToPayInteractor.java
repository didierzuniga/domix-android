package co.domix.android.customizer.interactor;

/**
 * Created by unicorn on 1/14/2018.
 */

public interface TotalToPayInteractor {
    void queryOrderToPay(String uid, int payMethod);
    void responseTotalToPay(int totalToPayCash, double taxe, int minPayment, double payUCommission,
                            int payURate, String country);
}
