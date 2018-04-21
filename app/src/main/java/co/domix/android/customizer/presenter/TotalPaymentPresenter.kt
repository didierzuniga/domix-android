package co.domix.android.customizer.presenter

/**
 * Created by unicorn on 4/13/2018.
 */
interface TotalPaymentPresenter {
    fun queryOrderToPay(uid: String, payMethod: Int)
    fun responseTotalToPayCash(commissionDomix: String, payTaxe: String, payTotalToDomix: String,
                               minPayment: String, enableButtonPay: Boolean)
}