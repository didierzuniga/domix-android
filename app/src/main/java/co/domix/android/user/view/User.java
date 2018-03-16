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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import co.domix.android.user.presenter.UserPresenter;
import co.domix.android.user.presenter.UserPresenterImpl;

public class User extends AppCompatActivity implements UserView, LocationListener {

    protected LocationManager locationManager;
    private ScrollView scrollView;
    private RadioGroup radioGroup;
    private LinearLayout linearNotInternet;
    private Button buttonRequestOrder, buttonSendFullnameAndPhone, buttonRefresh;
    private TextView from, to, buttonSelectFrom, buttonSelectTo, paymentCash, paymentEcoin;
    private EditText description1, description2;
    private Spinner spinnerDimensions;
    private byte dimenSelected;
    private TextInputEditText firstName, lastName, phone;
    private String countryOrigen, cityOrigen;
    private byte payMethod;
    private int priceInCash, priceInEcoin;
    private ProgressBar progressBarRequest;
    private AlertDialog alert = null;
    private android.app.AlertDialog alertDialog;
    private SharedPreferences location;
    private boolean fieldsWasFill;
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


        app = (DomixApplication) getApplicationContext();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        scrollView = (ScrollView) findViewById(R.id.rootScroll);
        linearNotInternet = (LinearLayout) findViewById(R.id.notInternetUser);
        presenter = new UserPresenterImpl(this);
        progressBarRequest = (ProgressBar) findViewById(R.id.progressBarRequest);
        location = getSharedPreferences("Locate_prefs", MODE_PRIVATE);
        editor = location.edit();

        spinnerDimensions = (Spinner) findViewById(R.id.spinnerDimensions);
        spinnerDimensions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dimenSelected = (byte) spinnerDimensions.getSelectedItemId();
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
                switch(checkedId) {
                    case R.id.payWithCash:
                        payMethod = 0;
                        break;
                    case R.id.payWithCredit:
                        payMethod = 1;
                        break;
                    case R.id.payWithEcoin:
                        payMethod = 2;
                        break;
                }
            }
        });

        description1 = (EditText) findViewById(R.id.idOrderDescription1);
        description2 = (EditText) findViewById(R.id.idOrderDescription2);
        paymentCash = (TextView) findViewById(R.id.idMoneyToPay);
        paymentEcoin = (TextView) findViewById(R.id.idEcoinToPay);

        buttonSelectFrom = (TextView) findViewById(R.id.buttonSelectFrom);
        buttonSelectFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("whatAddress", 0);
                editor.commit();
                goPickMap();
            }
        });

        buttonSelectTo = (TextView) findViewById(R.id.buttonSelectTo);
        buttonSelectTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("whatAddress", 1);
                editor.commit();
                goPickMap();
            }
        });

        buttonRequestOrder = (Button) findViewById(R.id.buttonRequestOrder);
        buttonRequestOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.GONE);
                showProgressBar();
                presenter.request(fieldsWasFill, app.uId, app.email, countryOrigen, cityOrigen,
                                from.getText().toString(), to.getText().toString(),
                                description1.getText().toString(), description2.getText().toString(),
                                dimenSelected, payMethod, priceInCash, priceInEcoin, User.this);
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

        buttonSendFullnameAndPhone = (Button) view.findViewById(R.id.buttonSendContactData);
        firstName = (TextInputEditText) view.findViewById(R.id.firstName);
        lastName = (TextInputEditText) view.findViewById(R.id.lastName);
        phone = (TextInputEditText) view.findViewById(R.id.phone);

        firstName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        lastName.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );

        buttonSendFullnameAndPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String getFirstName = firstName.getText().toString();
                String getLastName = lastName.getText().toString();
                String getPhone = phone.getText().toString();
                if (getFirstName.equals("") || getLastName.equals("") || getPhone.equals("")) {
                    Toast.makeText(User.this, R.string.toast_please_complete_all_files, Toast.LENGTH_SHORT).show();
                } else {
                    sendContactData(getFirstName, getLastName, getPhone);
                    alertDialog.dismiss();
                }
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
        from.setText(fromm);
    }

    @Override
    public void responseToName(String too) {
        to.setText(too);
    }

    @Override
    public void responseEmptyFields(String toastMessage) {
        hideProgressBar();
        scrollView.setVisibility(View.VISIBLE);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void responseCash(int priceInCashh, String countryO, String countryOrigenn, String cityOrigenn, int priceInEcoinn) {
        countryOrigen = countryOrigenn;
        cityOrigen = cityOrigenn;
        priceInCash = priceInCashh;
        priceInEcoin = priceInEcoinn;
        paymentCash.setText(" " + priceInCash + " " + countryO);
        paymentEcoin.setText(" " + String.valueOf(priceInEcoin) + " " + "eCoin");
    }

    @Override
    public void showNotInternet() {
        hideProgressBar();
        scrollView.setVisibility(View.GONE);
        linearNotInternet.setVisibility(View.VISIBLE);
    }

    @Override
    public void showYesInternet() {
        linearNotInternet.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        progressBarRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        presenter.requestForFullnameAndPhone(app.uId);
        progressBarRequest.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        from = (TextView) findViewById(R.id.idFrom);
        to = (TextView) findViewById(R.id.idTo);
        presenter.requestGeolocationAndDistance(location.getString("latFrom", ""),
                                                location.getString("lonFrom", ""),
                                                location.getString("latTo", ""),
                                                location.getString("lonTo", ""),
                                                location.getInt("whatAddress", 2),
                                                this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.verifyLocationAndInternet(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.clear().commit();
    }

    @Override
    public void onLocationChanged(Location loc) {
        editor.putString("latitude", String.valueOf(loc.getLatitude()));
        editor.putString("longitude", String.valueOf(loc.getLongitude()));
        editor.commit();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
