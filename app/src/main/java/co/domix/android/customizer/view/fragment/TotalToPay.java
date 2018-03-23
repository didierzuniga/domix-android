package co.domix.android.customizer.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.presenter.TotalToPayPresenter;
import co.domix.android.customizer.presenter.TotalToPayPresenterImpl;

/**
 * A simple {@link Fragment} subclass.
 */
public class TotalToPay extends Fragment implements TotalToPayView {

    private View view;
    private ProgressBar progressBarPay;
    private LinearLayout linearPayPerDomicilies, linearPaytaxes, linearPayTotal;
    private TextView toPayDomix, toPayDomixTotal, toPayTaxe;
    private DomixApplication app;
    private TotalToPayPresenter presenter;

    public TotalToPay() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_total_to_pay, container, false);
        presenter = new TotalToPayPresenterImpl(this);
        app = (DomixApplication) getActivity().getApplicationContext();

        progressBarPay = (ProgressBar) view.findViewById(R.id.progressBarPay);
        showProgressBar();

        linearPayPerDomicilies = (LinearLayout) view.findViewById(R.id.linearPayPerDomicilies);
        linearPaytaxes = (LinearLayout) view.findViewById(R.id.linearPayTaxes);
        linearPayTotal = (LinearLayout) view.findViewById(R.id.linearPayTotal);
        linearPayPerDomicilies.setVisibility(View.GONE);
        linearPaytaxes.setVisibility(View.GONE);
        linearPayTotal.setVisibility(View.GONE);
        toPayDomix = (TextView) view.findViewById(R.id.toPayDomix);
        toPayDomixTotal = (TextView) view.findViewById(R.id.toPayDomixTotal);
        toPayTaxe = (TextView) view.findViewById(R.id.toPayTaxe);

        presenter.queryOrderToPay(app.uId, app.payMethod);

        return view;
    }

    @Override
    public void showProgressBar() {
        progressBarPay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarPay.setVisibility(View.GONE);
    }

    @Override
    public void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix) {
        hideProgressBar();
        linearPayPerDomicilies.setVisibility(View.VISIBLE);
        linearPaytaxes.setVisibility(View.VISIBLE);
        linearPayTotal.setVisibility(View.VISIBLE);
        toPayDomix.setText(commissionDomix);
        toPayTaxe.setText(payTaxe);
        toPayDomixTotal.setText(payTotalToDomix);
    }
}
