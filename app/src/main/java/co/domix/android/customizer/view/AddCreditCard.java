package co.domix.android.customizer.view;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import co.domix.android.R;
import co.domix.android.utils.ToastsKt;
import kotlin.text.Regex;

public class AddCreditCard extends AppCompatActivity {

    private TextInputEditText numCreditCard, expireDate, securityCode;
    private TextView invalidCardNumber, invalidExpDate;
    private boolean checkedNumCreditCard = false, checkedExpiredDate = false, checkedSecureCode = false;
    private Button btnNext;
    private String mLastInput = "";
    private ImageView imgCard;
    private int card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_add_card));

        imgCard = (ImageView) findViewById(R.id.imgCards);
        invalidCardNumber = (TextView) findViewById(R.id.invalidNumber);
        invalidExpDate = (TextView) findViewById(R.id.invalidExpiredDate);
        securityCode = (TextInputEditText) findViewById(R.id.idSecureCode);
        securityCode.setHint(getString(R.string.text_code_visacard));
        numCreditCard = (TextInputEditText) findViewById(R.id.numberCreditCard);
        expireDate = (TextInputEditText) findViewById(R.id.expDate);
        numCreditCard.addTextChangedListener(filterText);
        expireDate.addTextChangedListener(filterExpireDate);
        securityCode.addTextChangedListener(filterSecureCode);
        btnNext = (Button) findViewById(R.id.idButtonNext);
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastsKt.toastShort(AddCreditCard.this, "Payments in test");
            }
        });

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

            if (s.length() == 1) {
                if (regVisa.matches(s)) {
                    card = 1;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_visa));
                    securityCode.setHint(getString(R.string.text_code_visacard));
                    setSecureCodeLenght(3);
                    setNumCreditCardLenght(16);
                } else {
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_creditcard_demo));
                }
            } else {
                if (regMaster.matches(s)) {
                    card = 2;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_mastercard));
                    securityCode.setHint(getString(R.string.text_code_mastercard));
                    setSecureCodeLenght(3);
                    setNumCreditCardLenght(16);
                } else if (regExpress.matches(s)) {
                    card = 3;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_amex));
                    securityCode.setHint(getString(R.string.text_code_express));
                    setSecureCodeLenght(4);
                    setNumCreditCardLenght(15);
                } else if (regDiscover.matches(s)) {
                    card = 4;
                    imgCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_discover));
                    securityCode.setHint(getString(R.string.text_code_discover));
                    setSecureCodeLenght(4);
                    setNumCreditCardLenght(16);
                } else if (nothing.matches(s)) {
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
            Regex regJCB = new Regex("^(?:2131|1800|35\\d{3})\\d{11}$");
            Regex nothing = new Regex("^$");

            if (regVisa.matches(s) || regMaster.matches(s) || regDiscover.matches(s)){
                securityCode.setText("");
                checkedNumCreditCard = true;
                willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
            } else if (regExpress.matches(s)){
                securityCode.setText("");
                checkedNumCreditCard = true;
                willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
            } else {
                checkedNumCreditCard = false;
                willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
            }

            numCreditCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (card == 1) {
                            if (regVisa.matches(s)) {
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 2) {
                            if (regMaster.matches(s)) {
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 3) {
                            if (regExpress.matches(s)) {
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 4) {
                            if (regDiscover.matches(s)) {
                                invalidCardNumber.setVisibility(View.GONE);
                            } else {
                                invalidCardNumber.setVisibility(View.VISIBLE);
                            }
                        } else if (card == 0) {
                            invalidCardNumber.setVisibility(View.GONE);
                        }
                    } else {
                        invalidCardNumber.setVisibility(View.GONE);
                    }
                }
            });

        }
    };

    private TextWatcher filterExpireDate = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            final Regex verifyExpireDate = new Regex("^[0-1][1-9{10}{11}{12}]\\/[1-9][0-9]$");
            final Regex verifyExpireDateWithZero = new Regex("^[0][0]\\/[1-9][0-9]$");

            String input = s.toString();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.ENGLISH);
            Calendar expiryDateDate = Calendar.getInstance();
            try {
                expiryDateDate.setTime(formatter.parse(input));
            } catch (ParseException e) {
                if (s.length() == 2 && !mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        expireDate.setText(expireDate.getText().toString() + "/");
                        expireDate.setSelection(expireDate.getText().toString().length());
                    }
                } else if (s.length() == 2 && mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        expireDate.setText(expireDate.getText().toString().substring(0, 1));
                        expireDate.setSelection(expireDate.getText().toString().length());
                    } else {
                        expireDate.setText("");
                        expireDate.setSelection(expireDate.getText().toString().length());
                        Toast.makeText(getApplicationContext(), "Enter a valid month", Toast.LENGTH_LONG).show();
                    }
                } else if (s.length() == 1) {
                    int month = Integer.parseInt(input);
                    if (month > 1) {
                        expireDate.setText("0" + expireDate.getText().toString() + "/");
                        expireDate.setSelection(expireDate.getText().toString().length());
                    }
                } else {

                }
                mLastInput = expireDate.getText().toString();

            }

            if (expireDate.length() == 5){
                if (!verifyExpireDateWithZero.matches(expireDate.getText().toString())){
                    if (verifyExpireDate.matches(expireDate.getText().toString())){
                        checkedExpiredDate = true;
                        willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
                    } else {
                        invalidExpDate.setVisibility(View.VISIBLE);
                        checkedExpiredDate = false;
                        willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
                    }
                } else {
                    invalidExpDate.setVisibility(View.VISIBLE);
                    checkedExpiredDate = false;
                    willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
                }

            } else {
                checkedExpiredDate = false;
                invalidExpDate.setVisibility(View.GONE);
                willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
            }
        }
    };

    private TextWatcher filterSecureCode = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() <= 3 && card == 1 || card == 2 || card == 4){
                if (securityCode.getText().length() == 3){
                    checkedSecureCode = true;
                    willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
                } else {
                    checkedSecureCode = false;
                    willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
                }
            } else if (s.length() <= 4 && card == 3){
                if (securityCode.getText().length() == 4){
                    checkedSecureCode = true;
                    willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
                } else {
                    checkedSecureCode = false;
                    willBeEnableButton(checkedNumCreditCard, checkedExpiredDate, checkedSecureCode);
                }
            }
        }
    };

    public void setSecureCodeLenght(int max){
        securityCode.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(max)
        });
    }

    public void setNumCreditCardLenght(int max){
        numCreditCard.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(max)
        });
    }

    public void willBeEnableButton(boolean chkNum, boolean chkExp, boolean chkSec){
        if (chkNum == true && chkExp == true && chkSec == true){
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
