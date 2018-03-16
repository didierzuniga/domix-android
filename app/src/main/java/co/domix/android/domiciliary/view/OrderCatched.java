package co.domix.android.domiciliary.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.presenter.OrderCatchedPresenter;
import co.domix.android.domiciliary.presenter.OrderCatchedPresenterImpl;
import co.domix.android.domiciliary.service.CoordinateService;

/**
 * Created by unicorn on 11/13/2017.
 */

public class OrderCatched extends AppCompatActivity implements OrderCatchedView {

    private DomixApplication app;
    private TextView textViewRequestedBy, textViewPhoneRequestedBy, textViewFrom, textViewTo,
                        textViewHeader, textViewDescription, textViewMoneyToPay;
    private String countryO;
    private double oriLat, oriLon, desLat, desLon;
    private Button cancelService, finishService, btnViewMap;
    private ImageButton call;
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
        startService(new Intent(this, CoordinateService.class));

        presenter = new OrderCatchedPresenterImpl(this);
        location = getSharedPreferences("Locate_prefs", MODE_PRIVATE);
        editor = location.edit();

        textViewRequestedBy = (TextView) findViewById(R.id.d_requested_by);
        textViewPhoneRequestedBy = (TextView) findViewById(R.id.d_phone_requested_by);
        call = (ImageButton) findViewById(R.id.idCall);
        textViewFrom = (TextView) findViewById(R.id.d_from);
        textViewTo = (TextView) findViewById(R.id.d_to);
        textViewHeader = (TextView) findViewById(R.id.d_header);
        textViewDescription = (TextView) findViewById(R.id.d_description);
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
                dialogFinish(app.uId);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.toast_can_not_backpressed_domiciliary), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getUserRequest() {
        presenter.getUserRequest(app.idOrder, app.uId, this);
    }

    @Override
    public void responseUserRequested(String nameAuthor, String cellphoneAuthor, String countryAuthor, String cityAuthor,
                                      String fromAuthor, String toAuthor, String titleAuthor,
                                      String descriptionAuthor, String oriLa, String oriLo,
                                      String desLa, String desLo, int moneyAuthor) {
        if (countryAuthor.equals("CO")){
            countryO = "COP";
        } else if (countryAuthor.equals("CL")){
            countryO = "CLP";
        }
        textViewRequestedBy.setText(nameAuthor);
        textViewPhoneRequestedBy.setText(cellphoneAuthor);
        textViewHeader.setText(titleAuthor);
        textViewDescription.setText(descriptionAuthor);
        textViewMoneyToPay.setText(String.valueOf(moneyAuthor)+" "+countryO);
        oriLat = Double.valueOf(oriLa);
        oriLon = Double.valueOf(oriLo);
        desLat = Double.valueOf(desLa);
        desLon = Double.valueOf(desLo);
    }

    @Override
    public void goPreviewRouteOrder() {
        Intent intent = new Intent(this, PreviewRouteOrder.class);
        intent.putExtra("latFrom", location.getString("latFrom", ""));
        intent.putExtra("latTo", location.getString("latTo", ""));
        intent.putExtra("lonFrom", location.getString("lonFrom", ""));
        intent.putExtra("lonTo", location.getString("lonTo", ""));
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

    @Override
    public void dialogFinish(String uidDomicili) {
        presenter.dialogFinish(String.valueOf(app.idOrder), uidDomicili, this);
    }

    @Override
    public void responseBackDomiciliaryActivity() {
        stopService(new Intent(this, CoordinateService.class));
        Intent intent = new Intent(this, Domiciliary.class);
        startActivity(intent);
        super.finish();
    }

    @Override
    public void goRateDomiciliary() {
        stopService(new Intent(this, CoordinateService.class));
        Intent intent = new Intent(this, DomiciliaryScore.class);
        startActivity(intent);
        finish();
    }
}
