package co.domix.android.customizer.view.fragment;

/**
 * Created by unicorn on 1/14/2018.
 */

public interface TotalToPayView {
    void showProgressBar();
    void hideProgressBar();
    void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix);
}
