package co.domix.android.domiciliary.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.presenter.OrderCatchedPresenter;
import co.domix.android.domiciliary.presenter.OrderCatchedPresenterImpl;
import co.domix.android.services.CoordinateServiceDeliveryman;
import co.domix.android.services.CoordinateServiceDeliverymanGoogleAPI;

/**
 * Created by unicorn on 11/13/2017.
 */

public class OrderCatched extends AppCompatActivity implements OrderCatchedView {

    private DomixApplication app;
    private TextView textViewRequestedBy, textViewPhoneRequestedBy, textViewFrom, textViewTo,
            textViewDescription1, textViewDescription2, textViewMoneyToPay, txtDeliverymanNotReceives,
            txtWouldReceive, txtDeliverymanReceives;
    private String oriCoordinate, desCoordinate;
    private Button cancelService, finishService, btnViewMap;
    private ImageButton call;
    private ProgressBar progressBar;
    private ScrollView scrollview;
    private SharedPreferences location;
    private SharedPreferences.Editor editor;
    private OrderCatchedPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_catched);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_delivery_detail));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        app = (DomixApplication) getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startService(new Intent(this, CoordinateServiceDeliverymanGoogleAPI.class));
        } else {
            startService(new Intent(this, CoordinateServiceDeliverymanGoogleAPI.class));
        }

        presenter = new OrderCatchedPresenterImpl(this);
        location = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = location.edit();

        progressBar = (ProgressBar) findViewById(R.id.progressCatched);
        scrollview = (ScrollView) findViewById(R.id.scrollCatched);
        textViewRequestedBy = (TextView) findViewById(R.id.d_requested_by);
        textViewPhoneRequestedBy = (TextView) findViewById(R.id.d_phone_requested_by);
        call = (ImageButton) findViewById(R.id.idCall);
        textViewFrom = (TextView) findViewById(R.id.txtVieFrom);
        textViewTo = (TextView) findViewById(R.id.txtVieTo);
        textViewDescription1 = (TextView) findViewById(R.id.txtVieDescription1);
        textViewDescription2 = (TextView) findViewById(R.id.txtVieDescription2);
        textViewMoneyToPay = (TextView) findViewById(R.id.d_moneyToPay);
        txtDeliverymanNotReceives = (TextView) findViewById(R.id.idDeliverymanNotReceives);
        txtDeliverymanReceives = (TextView) findViewById(R.id.idDeliverymanReceives);
        txtWouldReceive = (TextView) findViewById(R.id.idWouldReceive);
        textViewMoneyToPay = (TextView) findViewById(R.id.d_moneyToPay);

        getUserRequest();

        btnViewMap = (Button) findViewById(R.id.buttonViewMap);
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPreviewRouteOrder();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone(textViewPhoneRequestedBy.getText().toString());
            }
        });
        cancelService = (Button) findViewById(R.id.buttonCancelService);
        finishService = (Button) findViewById(R.id.buttonFinishService);
        cancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancel();
            }
        });
        finishService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFinish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.toast_can_not_backpressed_domiciliary), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void getUserRequest() {
        presenter.getUserRequest(app.idOrder, app.uId, this);
    }

    @Override
    public void responseUserRequested(String nameAuthor, String cellphoneAuthor, String countryAuthor, String cityAuthor,
                                      String fromAuthor, String toAuthor, String description1,
                                      String description2, String origenCoordinate, String destineCoordinate,
                                      int totalCostDelivery, boolean cashReceivesDeliveryman, int moneyCash) {
        if (cashReceivesDeliveryman){
            txtWouldReceive.setVisibility(View.VISIBLE);
            txtDeliverymanReceives.setVisibility(View.VISIBLE);
            txtDeliverymanReceives.setText(" " + moneyCash);
        } else {
            txtDeliverymanNotReceives.setVisibility(View.VISIBLE);
        }
        textViewRequestedBy.setText(nameAuthor);
        textViewPhoneRequestedBy.setText(cellphoneAuthor);
        textViewDescription1.setText(description1);
        textViewDescription2.setText(description2);
        textViewMoneyToPay.setText(String.valueOf(totalCostDelivery)+" "+countryAuthor);
        oriCoordinate = origenCoordinate;
        desCoordinate = destineCoordinate;
    }

    @Override
    public void goPreviewRouteOrder() {
        Intent intent = new Intent(this, PreviewRouteOrder.class);
        intent.putExtra("coordinateFromView", oriCoordinate);
        intent.putExtra("coordinateToView", desCoordinate);
        startActivity(intent);
    }

    @Override
    public void dialContactPhone(String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    @Override
    public void dialogCancel() {
        presenter.dialogCancel(String.valueOf(app.idOrder), app.uId, this);
    }

    public void dialogFinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_finish_request);
        builder.setPositiveButton(R.string.message_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scrollview.setVisibility(View.GONE);
                        showProgressBar();
                        presenter.dialogFinish(String.valueOf(app.idOrder), app.uId);
                    }
                }
        )
                .setNegativeButton(R.string.message_no, null);
        builder.create().show();
    }

    @Override
    public void showToastDeliverymanCancelledOrder() {
        Toast.makeText(this, R.string.toast_you_has_cancelled_order, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToastUserCancelledOrder() {
        Toast.makeText(this, R.string.toast_user_has_cancelled_order, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void responseBackDomiciliaryActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stopService(new Intent(this, CoordinateServiceDeliverymanGoogleAPI.class));
        } else {
            stopService(new Intent(this, CoordinateServiceDeliverymanGoogleAPI.class));
        }
        Intent intent = new Intent(this, Domiciliary.class);
        startActivity(intent);
        super.finish();
    }

    @Override
    public void goRateDomiciliary() {
        hideProgressBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stopService(new Intent(this, CoordinateServiceDeliverymanGoogleAPI.class));
        } else {
            stopService(new Intent(this, CoordinateServiceDeliverymanGoogleAPI.class));
        }
        Intent intent = new Intent(this, DomiciliaryScore.class);
        startActivity(intent);
        finish();
    }
}
