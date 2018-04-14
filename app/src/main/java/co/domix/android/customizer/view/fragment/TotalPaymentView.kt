package co.domix.android.customizer.view.fragment

/**
 * Created by unicorn on 4/13/2018.
 */
interface TotalPaymentView {
    fun showProgressBar()
    fun hideProgressBar()
    fun responseTotalToPayCash(commissionDomix:String, payTaxe:String , payTotalToDomix:String,
                               minPayment:String, enableButtonPay:Boolean)
}