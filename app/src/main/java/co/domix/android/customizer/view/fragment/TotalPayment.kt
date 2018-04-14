package co.domix.android.customizer.view.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import co.domix.android.R
import kotlinx.android.synthetic.main.fragment_total_payment.*


/**
 * A simple [Fragment] subclass.
 */
class TotalPayment : Fragment(), TotalPaymentView {

    private var enablePayment:Boolean = false
    private var miniPayment:String = ""

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_total_payment, container, false)
    }

    override fun showProgressBar() {
        prgBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        prgBar.visibility = View.GONE
    }

    override fun responseTotalToPayCash(commissionDomix: String, payTaxe: String, payTotalToDomix: String,
                                        minPayment: String, enableButtonPay: Boolean) {
        btnGoToPay.isEnabled = true
        enablePayment = true
        miniPayment = minPayment
        if (!enableButtonPay){
            enablePayment = false
            Toast.makeText(activity, "$(getString(R.string.text_minimum_amount) es $miniPayment", Toast.LENGTH_LONG).show()
        }
    }

}// Required empty public constructor
