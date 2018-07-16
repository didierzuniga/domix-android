package co.domix.android.customizer.view;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.domix.android.R;
import kotlin.text.Regex;

public class AddCreditCard extends AppCompatActivity {

    private TextInputEditText numCreditCard;
    private TextView invalidCardNumber;
    private ImageView imgCard;
    private int card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar tarjeta");

        imgCard = (ImageView) findViewById(R.id.imgCards);
        invalidCardNumber = (TextView) findViewById(R.id.invalidNumber);
        numCreditCard = (TextInputEditText) findViewById(R.id.numberCreditCard);
        numCreditCard.addTextChangedListener(filterText);

    }

    private TextWatcher filterText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            Regex regVisa = new Regex("^4[0-9]{12}(?:[0-9]{3})?$");
//            Regex regMaster = new Regex("^5[1-5][0-9]{14}$");
//            Regex regExpress = new Regex("^3[47][0-9]{13}$");
//            Regex regDiners = new Regex("^3(?:0[0-5]|[68][0-9])[0-9]{11}$");
//            Regex regDiscover = new Regex("^6(?:011|5[0-9]{2})[0-9]{12}$");
//            Regex regJCB= new Regex("^(?:2131|1800|35\\d{3})\\d{11}$");
//            Log.w("jjj", "Digit: "+s);
//
//            if (regVisa.matches(s)) {
//                Log.w("jjj", "VISA");
//            } else if (regMaster.matches(s)) {
//                Log.w("jjj", "MASTER");
//            } else if (regExpress.matches(s)) {
//                Log.w("jjj", "AMEX");
//            } else if (regDiners.matches(s)) {
//                Log.w("jjj", "DINERS");
//            } else if (regDiscover.matches(s)) {
//                Log.w("jjj", "DISCOVER");
//            } else if (regJCB.matches(s)) {
//                Log.w("jjj", "JCB");
//            } else {
//                Log.w("jjj", "INVALID");
//            }

            Regex regVisa = new Regex("^4$");
            Regex regMaster = new Regex("^5[1-5]$");
            Regex regExpress = new Regex("^3[47]$");
            Regex regDiners = new Regex("^3(?:0[0-5]|[68][0-9])[0-9]$");
            Regex regDiscover = new Regex("^6[5]$");
            Regex nothing = new Regex("^$");

            //Visa = 1
            //Master = 2
            //AMEX = 3
            //Discover = 4
            //Nothing = 0

            if (s.length() == 1){
                if (regVisa.matches(s)){
                    card = 1;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_visa));
                } else {
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_creditcard_demo));
                }
            } else {
                if (regMaster.matches(s)) {
                    card = 2;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_mastercard));
                } else if (regExpress.matches(s)) {
                    card = 3;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_amex));
                } else if (regDiscover.matches(s)) {
                    card = 4;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_discover));
                } else if (nothing.matches(s)){
                    card = 0;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_creditcard_demo));
                }
            }
        }

        @Override
        public void afterTextChanged(final Editable s) {
            final Regex regVisa = new Regex("^4[0-9]{12}(?:[0-9]{3})?$");
            final Regex regMaster = new Regex("^5[1-5][0-9]{14}$");
            final Regex regExpress = new Regex("^3[47][0-9]{13}$");
            Regex regDiners = new Regex("^3(?:0[0-5]|[68][0-9])[0-9]{11}$");
            final Regex regDiscover = new Regex("^6(?:011|5[0-9]{2})[0-9]{12}$");
            Regex regJCB= new Regex("^(?:2131|1800|35\\d{3})\\d{11}$");
            Regex nothing = new Regex("^$");

            numCreditCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        if (card == 1){
                            if (regVisa.matches(s)){
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 2){
                            if (regMaster.matches(s)){
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 3){
                            if (regExpress.matches(s)){
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 4){
                            if (regDiscover.matches(s)){
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 0){
                            invalidCardNumber.setVisibility(View.GONE);
                        }
                    } else {
                        invalidCardNumber.setVisibility(View.GONE);
                    }
                }
            });





//            if (regVisa.matches(s)) {
//                Log.w("jjj", "VISA");
//            } else if (regMaster.matches(s)) {
//                Log.w("jjj", "MASTER");
//            } else if (regExpress.matches(s)) {
//                Log.w("jjj", "AMEX");
//            } else if (regDiners.matches(s)) {
//                Log.w("jjj", "DINERS");
//            } else if (regDiscover.matches(s)) {
//                Log.w("jjj", "DISCOVER");
//            } else if (regJCB.matches(s)) {
//                Log.w("jjj", "JCB");
//            } else {
//                Log.w("jjj", "INVALID");
//            }
        }
    };
}
