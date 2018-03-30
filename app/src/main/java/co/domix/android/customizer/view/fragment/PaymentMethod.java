package co.domix.android.customizer.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import co.domix.android.DomixApplication;
import co.domix.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentMethod extends Fragment {

    private Button buttonNext;
    private RadioGroup radioGroup;
    private View view;
    private DomixApplication app;

    public PaymentMethod() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        app = (DomixApplication) getActivity().getApplicationContext();
        buttonNext = (Button) view.findViewById(R.id.goNext);
        buttonNext.setEnabled(false);

        radioGroup = (RadioGroup) view.findViewById(R.id.rdGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.methEfecty:
                        app.payMethod = 1;
                        buttonNext.setEnabled(true);
                        break;
                    case R.id.methPse:
                        app.payMethod = 2;
                        buttonNext.setEnabled(true);
                        break;
                    case R.id.methCreditcard:
                        app.payMethod = 3;
                        buttonNext.setEnabled(true);
                        break;
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TotalToPay totalToPay = new TotalToPay();
                getFragmentManager().beginTransaction().replace(R.id.container, totalToPay)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}
