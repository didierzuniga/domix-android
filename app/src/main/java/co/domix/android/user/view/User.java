package co.domix.android.user.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.services.LocationService;
import co.domix.android.user.presenter.UserPresenter;
import co.domix.android.user.presenter.UserPresenterImpl;
import co.domix.android.utils.ToastsKt;

public class User extends AppCompatActivity implements UserView {



    private ScrollView scrollView;
    private RadioGroup radioGroup;
    private LinearLayout linearNotInternet, lnrSwiCredit, lnrPaymentMethod;
    private SwitchCompat switchCompat;
    private Button buttonRequestOrder, btnSendFullnameAndPhone, btnBack, buttonRefresh;
    private TextView txtFrom, txtTo, buttonSelectFrom, buttonSelectTo, paymentCash;
    private EditText description1, description2;
    private Spinner spiDimensions;
    private byte dimenSelected;
    private TextInputEditText firstName, lastName, phone;
    private String countryOrigen, cityOrigen, codeCountry;
    private byte payMethod;
    private int totalCostToDB, priceInCash, disBetweenPoints, mCredit, costDelDesCredit, updateCreditUserToDB, creditUsedToDB;
    private ProgressBar progressBarRequest;
    private AlertDialog alert = null;
    private android.app.AlertDialog alertDialog;
    private boolean fieldsWasFill;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private DomixApplication app;
    private UserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.text_make_your_order);
        presenter = new UserPresenterImpl(this);
        app = (DomixApplication) getApplicationContext();

        scrollView = (ScrollView) findViewById(R.id.rootScroll);
        lnrSwiCredit = (LinearLayout) findViewById(R.id.lnrSwiCredit);
        lnrPaymentMethod = (LinearLayout) findViewById(R.id.lnrPaymentMethod);
        switchCompat = (SwitchCompat) findViewById(R.id.swiCredit);
        linearNotInternet = (LinearLayout) findViewById(R.id.notInternetUser);
        progressBarRequest = (ProgressBar) findViewById(R.id.progressBarRequest);

        shaPref = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = shaPref.edit();

        spiDimensions = (Spinner) findViewById(R.id.spinnerDimensions);
        spiDimensions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dimenSelected = (byte) spiDimensions.getSelectedItemId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        presenter.requestForFullnameAndPhone(app.uId);

        radioGroup = (RadioGroup) findViewById(R.id.rdGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.payWithCash:
                        payMethod = 0;
                        break;
                    case R.id.payWithCredit:
                        payMethod = 1;
                        break;
                }
            }
        });

        description1 = (EditText) findViewById(R.id.idOrderDescription1);
        description2 = (EditText) findViewById(R.id.idOrderDescription2);
        paymentCash = (TextView) findViewById(R.id.idMoneyToPay);

        buttonSelectFrom = (TextView) findViewById(R.id.buttonSelectFrom);
        buttonSelectFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(false);
                editor.putInt("whatAddress", 0);
                editor.commit();
                goPickMap();
            }
        });

        buttonSelectTo = (TextView) findViewById(R.id.buttonSelectTo);
        buttonSelectTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(false);
                editor.putInt("whatAddress", 1);
                editor.commit();
                goPickMap();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    costDelDesCredit = priceInCash - mCredit;
                    if (costDelDesCredit < 0){
                        paymentCash.setText(" " + 0.00 + " " + codeCountry);
                        totalCostToDB = 0;
                        updateCreditUserToDB = costDelDesCredit * -1;
                        creditUsedToDB = mCredit - updateCreditUserToDB;
                        lnrPaymentMethod.setVisibility(View.GONE);
                        payMethod = 2;
                    } else {
                        paymentCash.setText(" " + costDelDesCredit + " " + codeCountry);
                        totalCostToDB = costDelDesCredit;
                        updateCreditUserToDB = 0;
                        creditUsedToDB = mCredit;
                        lnrPaymentMethod.setVisibility(View.VISIBLE);
                    }
                } else {
                    lnrPaymentMethod.setVisibility(View.VISIBLE);
                    totalCostToDB = priceInCash;
                    paymentCash.setText(" " + priceInCash + " " + codeCountry);
                }
            }
        });

        buttonRequestOrder = (Button) findViewById(R.id.buttonRequestOrder);
        buttonRequestOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.GONE);
                showProgressBar();
                int creditApplyToDB, updtCreditUserToDB;
                if (switchCompat.isChecked()){
                    creditApplyToDB = creditUsedToDB;
                    updtCreditUserToDB = updateCreditUserToDB;
                } else {
                    creditApplyToDB = 0;
                    updtCreditUserToDB = 0;
                }
                presenter.request(fieldsWasFill, app.uId, app.email, countryOrigen, cityOrigen,
                        txtFrom.getText().toString(), txtTo.getText().toString(), disBetweenPoints,
                        description1.getText().toString(), description2.getText().toString(),
                        dimenSelected, payMethod, totalCostToDB, creditApplyToDB, updtCreditUserToDB, User.this);
            }
        });

        buttonRefresh = (Button) findViewById(R.id.buttonRefreshUser);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                presenter.verifyLocationAndInternet(User.this);
            }
        });
    }

    @Override
    public void startGetLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(User.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ToastsKt.toastShort(User.this, "No podemos ofrecerte el servicio");
                return;
            } else {
                try{
                    LocationManager locManager;
                    Location loc;
                    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    editor.putString("latitude", String.valueOf(loc.getLatitude()));
                    editor.putString("longitude",String.valueOf(loc.getLongitude()));
                    editor.commit();
                } catch (Exception e){
                    Log.w("jjj", "Exception-> "+e);
                }

            }
        } else {
            startService(new Intent(this, LocationService.class));
        }
    }

    @Override
    public void responseForFullnameAndPhone(boolean result) {
        fieldsWasFill = result;
    }

    @Override
    public void alertNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_location_deactivate)
                .setCancelable(false)
                .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User.super.finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void openDialogSendContactData() {
        scrollView.setVisibility(View.VISIBLE);
        hideProgressBar();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_send_user_contact, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSendFullnameAndPhone = view.findViewById(R.id.btnSendContactData);
        btnBack = view.findViewById(R.id.btnBack);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        phone = view.findViewById(R.id.phone);

        firstName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        lastName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );

        btnSendFullnameAndPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String getFirst_name = firstName.getText().toString();
                String getLast_name = lastName.getText().toString();
                String getPhone = phone.getText().toString();
                if (getFirst_name.equals("") || getLast_name.equals("") || getPhone.equals("")) {
                    Toast.makeText(User.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    sendContactData(getFirst_name, getLast_name, getPhone);
                    alertDialog.dismiss();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    public void sendContactData(String firstName, String lastName, String phone) {
        scrollView.setVisibility(View.GONE);
        showProgressBar();
        presenter.sendContactData(app.uId, firstName, lastName, phone, this);
    }

    @Override
    public void responseSuccessRequest(int getCountFull) {
        app.idOrder = getCountFull;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        Intent intent = new Intent(User.this, Requested.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void resultErrorRequest() {
        scrollView.setVisibility(View.VISIBLE);
        hideProgressBar();
        Toast.makeText(this, R.string.toast_error_request, Toast.LENGTH_LONG).show();
    }

    @Override
    public void contactDataSent() {
        fieldsWasFill = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.requestForFullnameAndPhone(app.uId);
                        hideProgressBar();
                        buttonRequestOrder.callOnClick();
                    }
                }, 3000);
            }
        });
    }

    @Override
    public void goPickMap() {
        Intent intent = new Intent(this, PickMap.class);
        startActivity(intent);
    }

    @Override
    public void responseFromName(String fromm) {
        txtFrom.setText(fromm);
    }

    @Override
    public void responseToName(String too) {
        txtTo.setText(too);
    }

    @Override
    public void responseEmptyFields(String toastMessage) {
        hideProgressBar();
        scrollView.setVisibility(View.VISIBLE);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void responseCash(int priceInCashh, String countryO, String countryOrigenn, String cityOrigenn,
                             int distanceBetweenPoints, int myCredit) {
        mCredit = myCredit;
        codeCountry = countryO;
        if (myCredit > 0){
            lnrSwiCredit.setVisibility(View.VISIBLE);
            switchCompat.setHint("Usar crédito " + myCredit + " " + countryO);
        }
        countryOrigen = countryOrigenn;
        cityOrigen = cityOrigenn;
        priceInCash = priceInCashh;
        disBetweenPoints = distanceBetweenPoints;
        paymentCash.setText(" " + priceInCash + " " + countryO);
    }

    @Override
    public void showNotInternet() {
        hideProgressBar();
        scrollView.setVisibility(View.GONE);
        linearNotInternet.setVisibility(View.VISIBLE);
    }

    @Override
    public void showYesInternet() {
        try {
            linearNotInternet.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        } catch (Exception e){

        }
    }

    @Override
    public void showProgressBar() {
        progressBarRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        try {
            progressBarRequest.setVisibility(View.GONE);
        } catch (Exception e){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.verifyLocationAndInternet(this);
        txtFrom = (TextView) findViewById(R.id.idFrom);
        txtTo = (TextView) findViewById(R.id.idTo);

        try {
            presenter.requestGeolocationAndDistance(app.uId, shaPref.getString("latFrom", ""),
                    shaPref.getString("lonFrom", ""),
                    shaPref.getString("latTo", ""),
                    shaPref.getString("lonTo", ""),
                    shaPref.getInt("whatAddress", 2),
                    this);
        } catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        presenter.verifyLocationAndInternet(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.remove("latFrom");
        editor.remove("lonFrom");
        editor.remove("latTo");
        editor.remove("lonTo");
        editor.commit();
    }
}
